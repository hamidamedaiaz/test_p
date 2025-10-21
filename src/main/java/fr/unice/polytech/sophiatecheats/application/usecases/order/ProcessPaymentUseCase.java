package fr.unice.polytech.sophiatecheats.application.usecases.order;

import fr.unice.polytech.sophiatecheats.application.dto.order.ProcessPaymentRequest;
import fr.unice.polytech.sophiatecheats.application.dto.order.ProcessPaymentResponse;
import fr.unice.polytech.sophiatecheats.application.usecases.UseCase;
import fr.unice.polytech.sophiatecheats.domain.entities.order.Order;
import fr.unice.polytech.sophiatecheats.domain.entities.restaurant.Restaurant;
import fr.unice.polytech.sophiatecheats.domain.entities.user.User;
import fr.unice.polytech.sophiatecheats.domain.enums.PaymentMethod;
import fr.unice.polytech.sophiatecheats.domain.exceptions.EntityNotFoundException;
import fr.unice.polytech.sophiatecheats.domain.exceptions.InsufficientCreditException;
import fr.unice.polytech.sophiatecheats.domain.exceptions.PaymentTimeoutException;
import fr.unice.polytech.sophiatecheats.domain.repositories.OrderRepository;
import fr.unice.polytech.sophiatecheats.domain.repositories.RestaurantRepository;
import fr.unice.polytech.sophiatecheats.domain.repositories.UserRepository;
import fr.unice.polytech.sophiatecheats.domain.services.PaymentService;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * Use case pour traiter le paiement d'une commande avec gestion automatique du timeout.
 *
 * <p>Ce use case implémente la troisième étape critique du flux métier principal:
 * <strong>Order → Slot → Payment → Confirmation</strong></p>
 *
 * <h3>Responsabilités:</h3>
 * <ul>
 *   <li>Validation des conditions préalables au paiement</li>
 *   <li>Gestion du timeout de paiement (15 minutes)</li>
 *   <li>Traitement des deux méthodes de paiement (crédit étudiant / carte externe)</li>
 *   <li>Libération automatique des créneaux en cas d'échec</li>
 *   <li>Mise à jour transactionnelle des états</li>
 * </ul>
 *
 * <h3>Flux de traitement:</h3>
 * <ol>
 *   <li>Validation de l'existence de la commande et du créneau réservé</li>
 *   <li>Vérification du respect du timeout (15 minutes depuis création)</li>
 *   <li>Traitement du paiement selon la méthode choisie</li>
 *   <li>En cas de succès: marquage de la commande comme payée</li>
 *   <li>En cas d'échec: libération du créneau et expiration de la commande</li>
 * </ol>
 *
 * <h3>Règles métier appliquées:</h3>
 * <ul>
 *   <li><strong>Timeout strict:</strong> 15 minutes maximum entre création et paiement</li>
 *   <li><strong>Atomicité:</strong> Échec de paiement = libération automatique du créneau</li>
 *   <li><strong>Pas de paiement mixte:</strong> Une seule méthode par commande</li>
 *   <li><strong>Validation des fonds:</strong> Vérification préalable pour le crédit étudiant</li>
 * </ul>
 *
 * @author SophiaTech Eats Backend Team
 * @since 1.0
 * @see Order
 * @see PaymentService
 */
public class ProcessPaymentUseCase implements UseCase<ProcessPaymentRequest, ProcessPaymentResponse> {

    /** Repository des commandes */
    private final OrderRepository orderRepository;

    /** Repository des utilisateurs (pour gestion du crédit étudiant) */
    private final UserRepository userRepository;

    /** Repository des restaurants (pour gestion des créneaux) */
    private final RestaurantRepository restaurantRepository;

    /** Service de traitement des paiements externes */
    private final PaymentService paymentService;

    /**
     * Timeout de paiement en minutes selon les spécifications métier.
     * Après ce délai, la commande expire et le créneau est libéré automatiquement.
     */
    private static final int PAYMENT_TIMEOUT_MINUTES = 15;

    /**
     * Constructeur avec injection des dépendances.
     *
     * @param orderRepository repository pour la persistance des commandes
     * @param userRepository repository pour la gestion des utilisateurs
     * @param restaurantRepository repository pour la gestion des restaurants
     * @param paymentService service pour le traitement des paiements externes
     */
    public ProcessPaymentUseCase(OrderRepository orderRepository,
                               UserRepository userRepository,
                               RestaurantRepository restaurantRepository,
                               PaymentService paymentService) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.restaurantRepository = restaurantRepository;
        this.paymentService = paymentService;
    }

    /**
     * Traite le paiement d'une commande avec toutes les vérifications nécessaires.
     *
     * <p>Cette méthode orchestre l'ensemble du processus de paiement en appliquant
     * les règles métier et en gérant automatiquement les cas d'erreur.</p>
     *
     * @param request les données de paiement (ID commande et éventuellement token carte)
     * @return le résultat du traitement avec statut de succès et détails
     * @throws IllegalArgumentException si la requête est invalide
     * @throws EntityNotFoundException si la commande n'existe pas
     * @throws IllegalStateException si la commande n'a pas de créneau réservé
     * @throws PaymentTimeoutException si le timeout est dépassé
     * @throws InsufficientCreditException si le crédit étudiant est insuffisant
     */
    @Override
    public ProcessPaymentResponse execute(ProcessPaymentRequest request) {
        validateRequest(request);

        // 1. Récupération et validation de la commande
        Order order = retrieveAndValidateOrder(request.orderId());

        // 2. Vérification du timeout
        validatePaymentTimeout(order);

        try {
            // 3. Traitement du paiement selon la méthode
            boolean paymentSuccess = processPaymentByMethod(order, request);

            if (paymentSuccess) {
                // 4. Succès - marquer comme payée
                return handlePaymentSuccess(order);
            } else {
                // 5. Échec - libérer le créneau
                return handlePaymentFailure(order, "Échec du traitement du paiement");
            }

        } catch (Exception e) {
            // En cas d'erreur, libérer le créneau automatiquement
            releaseSlotAndExpireOrder(order);
            throw e;
        }
    }

    /**
     * Valide la requête de paiement.
     *
     * @param request la requête à valider
     * @throws IllegalArgumentException si la requête est invalide
     */
    private void validateRequest(ProcessPaymentRequest request) {
        if (request == null || !request.isValid()) {
            throw new IllegalArgumentException("Requête de paiement invalide");
        }
    }

    /**
     * Récupère et valide l'existence de la commande avec ses prérequis.
     *
     * @param orderId l'identifiant de la commande
     * @return la commande validée
     * @throws EntityNotFoundException si la commande n'existe pas
     * @throws IllegalStateException si la commande n'a pas de créneau réservé
     */
    private Order retrieveAndValidateOrder(String orderId) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new EntityNotFoundException("Commande non trouvée: " + orderId));

        if (!order.hasDeliverySlot()) {
            throw new IllegalStateException("La commande doit avoir un créneau réservé avant le paiement");
        }

        return order;
    }

    /**
     * Vérifie que le timeout de paiement n'est pas dépassé.
     *
     * <p>Si le timeout est dépassé, la commande est automatiquement expirée
     * et son créneau libéré.</p>
     *
     * @param order la commande à vérifier
     * @throws PaymentTimeoutException si le timeout est dépassé
     */
    private void validatePaymentTimeout(Order order) {
        LocalDateTime now = LocalDateTime.now();
        long minutesSinceOrder = ChronoUnit.MINUTES.between(order.getOrderDateTime(), now);

        if (minutesSinceOrder > PAYMENT_TIMEOUT_MINUTES) {
            releaseSlotAndExpireOrder(order);
            throw new PaymentTimeoutException("Timeout de paiement dépassé pour la commande: " + order.getOrderId());
        }
    }

    /**
     * Traite le paiement selon la méthode spécifiée dans la commande.
     *
     * @param order la commande à payer
     * @param request les données de paiement
     * @return true si le paiement a réussi, false sinon
     */
    private boolean processPaymentByMethod(Order order, ProcessPaymentRequest request) {
        return switch (order.getPaymentMethod()) {
            case STUDENT_CREDIT -> processStudentCreditPayment(order);
            case EXTERNAL_CARD -> processExternalCardPayment(order, request);
        };
    }

    /**
     * Traite un paiement par crédit étudiant.
     *
     * <p>Vérifie la suffisance du solde et effectue la déduction atomique.</p>
     *
     * @param order la commande à payer
     * @return true si le paiement a réussi
     * @throws EntityNotFoundException si l'utilisateur n'existe pas
     * @throws InsufficientCreditException si le crédit est insuffisant
     */
    private boolean processStudentCreditPayment(Order order) {
        User user = userRepository.findById(order.getUser().getId())
            .orElseThrow(() -> new EntityNotFoundException("Utilisateur non trouvé"));

        if (!user.hasEnoughCredit(order.getTotalAmount())) {
            throw new InsufficientCreditException("Crédit étudiant insuffisant");
        }

        // Déduction atomique du crédit
        user.deductCredit(order.getTotalAmount());
        userRepository.save(user);

        return true;
    }

    /**
     * Traite un paiement par carte externe via le service dédié.
     *
     * @param order la commande à payer
     * @param request les données de paiement contenant le token carte
     * @return true si le paiement a réussi
     */
    private boolean processExternalCardPayment(Order order, ProcessPaymentRequest request) {
        return paymentService.processExternalPayment(
            order.getTotalAmount(),
            request.cardToken(),
            order.getOrderId()
        );
    }

    /**
     * Gère le succès du paiement en mettant à jour la commande.
     *
     * @param order la commande payée avec succès
     * @return la réponse de succès
     */
    private ProcessPaymentResponse handlePaymentSuccess(Order order) {
        order.markAsPaid();
        Order savedOrder = orderRepository.save(order);

        return new ProcessPaymentResponse(
            savedOrder.getOrderId(),
            true,
            "Paiement traité avec succès",
            savedOrder.getPaymentMethod(),
            savedOrder.getTotalAmount()
        );
    }

    /**
     * Gère l'échec du paiement en libérant les ressources.
     *
     * @param order la commande dont le paiement a échoué
     * @param errorMessage le message d'erreur à retourner
     * @return la réponse d'échec
     */
    private ProcessPaymentResponse handlePaymentFailure(Order order, String errorMessage) {
        releaseSlotAndExpireOrder(order);

        return new ProcessPaymentResponse(
            order.getOrderId(),
            false,
            errorMessage,
            order.getPaymentMethod(),
            order.getTotalAmount()
        );
    }

    /**
     * Libère le créneau réservé et marque la commande comme expirée.
     *
     * <p>Cette méthode assure la cohérence des données en cas d'échec de paiement
     * ou de timeout en libérant automatiquement les ressources réservées.</p>
     *
     * @param order la commande à expirer
     */
    private void releaseSlotAndExpireOrder(Order order) {
        // Libération du créneau si réservé
        if (order.hasDeliverySlot()) {
            Restaurant restaurant = restaurantRepository.findById(order.getRestaurant().getId())
                .orElse(null);

            if (restaurant != null) {
                restaurant.getDeliverySchedule().releaseSlot(order.getDeliverySlotId());
                restaurantRepository.save(restaurant);
            }

            order.releaseDeliverySlot();
        }

        // Expiration de la commande
        order.setStatus(fr.unice.polytech.sophiatecheats.domain.enums.OrderStatus.EXPIRED);
        orderRepository.save(order);
    }
}
