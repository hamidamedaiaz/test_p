package fr.unice.polytech.sophiatecheats.application.usecases.order;

import fr.unice.polytech.sophiatecheats.application.dto.order.PlaceOrderRequest;
import fr.unice.polytech.sophiatecheats.application.dto.order.PlaceOrderResponse;
import fr.unice.polytech.sophiatecheats.application.usecases.UseCase;
import fr.unice.polytech.sophiatecheats.domain.entities.order.Order;
import fr.unice.polytech.sophiatecheats.domain.entities.order.OrderItem;
import fr.unice.polytech.sophiatecheats.domain.entities.restaurant.Dish;
import fr.unice.polytech.sophiatecheats.domain.entities.restaurant.Restaurant;
import fr.unice.polytech.sophiatecheats.domain.entities.user.User;
import fr.unice.polytech.sophiatecheats.domain.enums.OrderStatus;
import fr.unice.polytech.sophiatecheats.domain.enums.PaymentMethod;
import fr.unice.polytech.sophiatecheats.domain.exceptions.EntityNotFoundException;
import fr.unice.polytech.sophiatecheats.domain.exceptions.InsufficientCreditException;
import fr.unice.polytech.sophiatecheats.domain.repositories.OrderRepository;
import fr.unice.polytech.sophiatecheats.domain.repositories.UserRepository;
import fr.unice.polytech.sophiatecheats.domain.repositories.RestaurantRepository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Use case pour passer une commande avec paiement par crédit étudiant.
 * L'utilisateur est identifié par son ID, pas besoin de login.
 */
public class PlaceOrderUseCase implements UseCase<PlaceOrderRequest, PlaceOrderResponse> {

    private final UserRepository userRepository;
    private final RestaurantRepository restaurantRepository;
    private final OrderRepository orderRepository;

    public PlaceOrderUseCase(UserRepository userRepository,
                           RestaurantRepository restaurantRepository,
                           OrderRepository orderRepository) {
        this.userRepository = userRepository;
        this.restaurantRepository = restaurantRepository;
        this.orderRepository = orderRepository;
    }

    @Override
    public PlaceOrderResponse execute(PlaceOrderRequest request) {
        if (request == null || !request.isValid()) {
            throw new IllegalArgumentException("Invalid request");
        }

        // Récupérer l'utilisateur (sans authentification, juste identification)
        User user = userRepository.findById(request.userId())
            .orElseThrow(() -> new EntityNotFoundException("User not found: " + request.userId()));

        // Récupérer le restaurant
        Restaurant restaurant = restaurantRepository.findById(request.restaurantId())
            .orElseThrow(() -> new EntityNotFoundException("Restaurant not found: " + request.restaurantId()));

        // Créer les items de commande et calculer le total
        List<OrderItem> orderItems = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (var requestItem : request.items()) {
            Dish dish = restaurant.findDishById(requestItem.dishId())
                .orElseThrow(() -> new EntityNotFoundException("Dish not found: " + requestItem.dishId()));

            OrderItem orderItem = new OrderItem(dish, requestItem.quantity());
            orderItems.add(orderItem);
            totalAmount = totalAmount.add(dish.getPrice().multiply(BigDecimal.valueOf(requestItem.quantity())));
        }

        // Vérifier le crédit étudiant si paiement par crédit
        if (request.paymentMethod() == PaymentMethod.STUDENT_CREDIT) {
            if (!user.hasEnoughCredit(totalAmount)) {
                throw new InsufficientCreditException("Insufficient student credit. Required: " +
                    totalAmount + ", Available: " + user.getStudentCredit());
            }
        }

        // Créer la commande
        Order order = new Order(
            user,
            restaurant,
            orderItems,
            request.paymentMethod()
        );

        // Déduire le crédit si nécessaire
        if (request.paymentMethod() == PaymentMethod.STUDENT_CREDIT) {
            user.deductCredit(totalAmount);
            userRepository.save(user);
        }

        // Sauvegarder la commande
        Order savedOrder = orderRepository.save(order);

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
