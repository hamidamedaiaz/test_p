package fr.unice.polytech.sophiatecheats.application.usecases.user.order;

import fr.unice.polytech.sophiatecheats.application.dto.user.PlaceOrderRequest;
import fr.unice.polytech.sophiatecheats.application.dto.user.PlaceOrderResponse;
import fr.unice.polytech.sophiatecheats.application.usecases.UseCase;
import fr.unice.polytech.sophiatecheats.domain.entities.cart.Cart;
import fr.unice.polytech.sophiatecheats.domain.entities.cart.CartItem;
import fr.unice.polytech.sophiatecheats.domain.entities.order.Order;
import fr.unice.polytech.sophiatecheats.domain.entities.order.OrderItem;
import fr.unice.polytech.sophiatecheats.domain.entities.restaurant.Dish;
import fr.unice.polytech.sophiatecheats.domain.entities.restaurant.Restaurant;
import fr.unice.polytech.sophiatecheats.domain.entities.user.User;
import fr.unice.polytech.sophiatecheats.domain.enums.PaymentMethod;
import fr.unice.polytech.sophiatecheats.domain.exceptions.EntityNotFoundException;
import fr.unice.polytech.sophiatecheats.domain.exceptions.InsufficientCreditException;
import fr.unice.polytech.sophiatecheats.domain.exceptions.ValidationException;
import fr.unice.polytech.sophiatecheats.domain.repositories.CartRepository;
import fr.unice.polytech.sophiatecheats.domain.repositories.OrderRepository;
import fr.unice.polytech.sophiatecheats.domain.repositories.UserRepository;
import fr.unice.polytech.sophiatecheats.domain.repositories.RestaurantRepository;
import fr.unice.polytech.sophiatecheats.domain.services.payment.PaymentContext;
import fr.unice.polytech.sophiatecheats.domain.services.payment.PaymentResult;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Use case pour transformer un panier en commande avec paiement.
 *
 * <p>Ce use case valide le panier actif de l'utilisateur et le transforme
 * en commande après vérification du paiement.</p>
 *
 * <h3>Flux nominal:</h3>
 * <ol>
 *   <li>Vérifie que l'utilisateur n'a pas de commande active</li>
 *   <li>Récupère le panier actif de l'utilisateur</li>
 *   <li>Utilise PaymentContext pour traiter le paiement avec la stratégie appropriée</li>
 *   <li>Transforme le panier en commande</li>
 *   <li>Sauvegarde la commande</li>
 *   <li>Vide le panier</li>
 * </ol>
 */
public class PlaceOrderUseCase implements UseCase<PlaceOrderRequest, PlaceOrderResponse> {

    private final UserRepository userRepository;
    private final RestaurantRepository restaurantRepository;
    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;

    public PlaceOrderUseCase(UserRepository userRepository,
                           RestaurantRepository restaurantRepository,
                           OrderRepository orderRepository,
                           CartRepository cartRepository) {
        this.userRepository = userRepository;
        this.restaurantRepository = restaurantRepository;
        this.orderRepository = orderRepository;
        this.cartRepository = cartRepository;
    }

    @Override
    public PlaceOrderResponse execute(PlaceOrderRequest request) {
        if (request == null || !request.isValid()) {
            throw new IllegalArgumentException("Invalid request");
        }

        // CONTRAINTE MÉTIER : Un utilisateur ne peut pas avoir plusieurs commandes en parallèle
        boolean hasActiveOrder = orderRepository.existsActiveOrderByUserId(request.userId());
        if (hasActiveOrder) {
            throw new ValidationException(
                "Vous avez déjà une commande en cours. Veuillez attendre qu'elle soit terminée avant d'en créer une nouvelle.");
        }

        // Récupérer l'utilisateur
        User user = userRepository.findById(request.userId())
            .orElseThrow(() -> new EntityNotFoundException("User not found: " + request.userId()));

        // Récupérer le panier actif de l'utilisateur
        Cart cart = cartRepository.findActiveCartByUserId(request.userId())
            .orElseThrow(() -> new ValidationException(
                "Aucun panier actif trouvé. Veuillez d'abord ajouter des plats à votre panier."));

        // Vérifier que le panier n'est pas vide
        if (cart.isEmpty()) {
            throw new ValidationException("Le panier est vide. Impossible de créer une commande.");
        }

        // Vérifier le délai d'expiration du panier (5 minutes)
        if (cart.getCreatedAt() != null && java.time.Duration.between(cart.getCreatedAt(), java.time.LocalDateTime.now()).toMinutes() > 5) {
            throw new ValidationException("Le délai pour valider votre panier est dépassé. Veuillez recommencer votre commande.");
        }

        // Récupérer le restaurant
        Restaurant restaurant = restaurantRepository.findById(request.restaurantId())
            .orElseThrow(() -> new EntityNotFoundException("Restaurant not found: " + request.restaurantId()));

        // Transformer les CartItems en OrderItems
        List<OrderItem> orderItems = new ArrayList<>();
        BigDecimal totalAmount = cart.calculateTotal();

        for (CartItem cartItem : cart.getItems()) {
            // Récupérer le plat pour créer l'OrderItem
            Dish dish = restaurant.findDishById(cartItem.getDishId())
                .orElseThrow(() -> new EntityNotFoundException("Dish not found: " + cartItem.getDishId()));

            OrderItem orderItem = new OrderItem(dish, cartItem.getQuantity());
            orderItems.add(orderItem);
        }

        // Utiliser le PaymentContext pour traiter le paiement avec la stratégie appropriée
        PaymentContext paymentContext = new PaymentContext(request.paymentMethod());

        // Vérifier si l'utilisateur peut payer
        if (!paymentContext.canUserPay(user, totalAmount)) {
            // Message d'erreur adapté selon la méthode de paiement
            if (request.paymentMethod() == PaymentMethod.STUDENT_CREDIT) {
                throw new InsufficientCreditException(
                    String.format("Crédit étudiant insuffisant. Requis: %.2f€, Disponible: %.2f€",
                        totalAmount.doubleValue(),
                        user.getStudentCredit().doubleValue())
                );
            } else {
                throw new ValidationException(
                    String.format("Paiement par carte bancaire impossible. Montant: %.2f€",
                        totalAmount.doubleValue())
                );
            }
        }

        // Traiter le paiement via la stratégie
        PaymentResult paymentResult = paymentContext.executePayment(totalAmount, user);

        // Vérifier le résultat du paiement
        if (!paymentResult.success()) {
            throw new ValidationException("Échec du paiement: " + paymentResult.message());
        }
        
        

        // Sauvegarder l'utilisateur UNIQUEMENT si le crédit étudiant a été modifié
        // (pas nécessaire pour les paiements externes)
        if (request.paymentMethod() == PaymentMethod.STUDENT_CREDIT) {
            userRepository.save(user);
        }

        // Créer la commande à partir du panier
        Order order = new Order(
            user,
            restaurant,
            orderItems,
            request.paymentMethod()
        );

        // Marquer automatiquement comme payé pour le crédit étudiant
        if (request.paymentMethod() == PaymentMethod.STUDENT_CREDIT) {
            order.markAsPaid();
        }

        // Sauvegarder la commande
        Order savedOrder = orderRepository.save(order);

        // Vider le panier apres transformation en commande
        cartRepository.delete(cart);

        return new PlaceOrderResponse(
            savedOrder.getOrderId(),
            savedOrder.getUser().getName(),
            savedOrder.getRestaurant().getName(),
            savedOrder.getTotalAmount(),
            savedOrder.getStatus(),
            savedOrder.getPaymentMethod(),
            savedOrder.getOrderDateTime()
        );
    }
}
