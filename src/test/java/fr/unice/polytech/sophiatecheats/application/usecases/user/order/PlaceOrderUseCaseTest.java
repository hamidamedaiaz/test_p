package fr.unice.polytech.sophiatecheats.application.usecases.user.order;

import fr.unice.polytech.sophiatecheats.application.dto.user.request.PlaceOrderRequest;
import fr.unice.polytech.sophiatecheats.application.dto.user.response.PlaceOrderResponse;
import fr.unice.polytech.sophiatecheats.domain.entities.cart.Cart;
import fr.unice.polytech.sophiatecheats.domain.entities.order.Order;
import fr.unice.polytech.sophiatecheats.domain.entities.restaurant.Dish;
import fr.unice.polytech.sophiatecheats.domain.entities.restaurant.Restaurant;
import fr.unice.polytech.sophiatecheats.domain.entities.user.User;
import fr.unice.polytech.sophiatecheats.domain.enums.OrderStatus;
import fr.unice.polytech.sophiatecheats.domain.enums.PaymentMethod;
import fr.unice.polytech.sophiatecheats.domain.exceptions.EntityNotFoundException;
import fr.unice.polytech.sophiatecheats.domain.exceptions.InsufficientCreditException;
import fr.unice.polytech.sophiatecheats.domain.exceptions.ValidationException;
import fr.unice.polytech.sophiatecheats.domain.repositories.CartRepository;
import fr.unice.polytech.sophiatecheats.domain.repositories.OrderRepository;
import fr.unice.polytech.sophiatecheats.domain.repositories.RestaurantRepository;
import fr.unice.polytech.sophiatecheats.domain.repositories.UserRepository;
import fr.unice.polytech.sophiatecheats.domain.services.payment.PaymentResult;
import fr.unice.polytech.sophiatecheats.domain.services.payment.PaymentStrategy;
import fr.unice.polytech.sophiatecheats.domain.services.payment.PaymentStrategyFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayName("Tests du Use Case - Passer une Commande (Cart → Order)")
class PlaceOrderUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RestaurantRepository restaurantRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private CartRepository cartRepository;

    private PlaceOrderUseCase useCase;

    private UUID userId;
    private UUID restaurantId;
    private User testUser;
    private Restaurant testRestaurant;
    private Dish testDish;
    private Cart testCart;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        useCase = new PlaceOrderUseCase(userRepository, restaurantRepository, orderRepository, cartRepository);

        // Initialisation des données de test
        userId = UUID.randomUUID();
        restaurantId = UUID.randomUUID();

        // Création d'un utilisateur avec 50€ de crédit
        testUser = new User("marcel@example.com", "Marcel Dupont");
        testUser.setStudentCredit(BigDecimal.valueOf(50.00));

        // Création d'un restaurant et d'un plat
        testRestaurant = new Restaurant("Le Tacos du Campus", "Tacos et burgers");
        testDish = Dish.builder()
                .name("Tacos 3 viandes")
                .description("Délicieux tacos")
                .price(BigDecimal.valueOf(8.50))
                .build();
        testRestaurant.addDish(testDish);

        // Création d'un Cart avec 2 tacos
        testCart = new Cart(userId);
        testCart.addDish(testDish, 2, testRestaurant.getId()); // 2 * 8.50 = 17.00€
    }

    /**
     * Test du scénario nominal : commande avec paiement par crédit étudiant.
     *
     * <p>Scénario:
     * - Marcel a un panier avec 2 tacos (17€)
     * - Il valide avec crédit étudiant (50€ disponible)
     * - Le paiement réussit
     * - Le Cart est transformé en Order
     * - Le Cart est supprimé</p>
     */
    @Test
    @DisplayName("Devrait transformer le Cart en Order avec crédit étudiant")
    void should_transform_cart_to_order_with_student_credit_successfully() {
        // Given
        PlaceOrderRequest request = new PlaceOrderRequest(
            userId,
            restaurantId,
            PaymentMethod.STUDENT_CREDIT
        );

        BigDecimal expectedTotal = BigDecimal.valueOf(17.0);
        BigDecimal expectedRemainingCredit = BigDecimal.valueOf(33.0); // 50 - 17

        // Mock : pas de commande active
        when(orderRepository.existsActiveOrderByUserId(userId)).thenReturn(false);

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(cartRepository.findActiveCartByUserId(userId)).thenReturn(Optional.of(testCart));
        when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.of(testRestaurant));

        Order savedOrder = mock(Order.class);
        when(savedOrder.getOrderId()).thenReturn(UUID.randomUUID().toString());
        when(savedOrder.getUser()).thenReturn(testUser);
        when(savedOrder.getRestaurant()).thenReturn(testRestaurant);
        when(savedOrder.getTotalAmount()).thenReturn(expectedTotal);
        when(savedOrder.getStatus()).thenReturn(OrderStatus.PENDING);
        when(savedOrder.getPaymentMethod()).thenReturn(PaymentMethod.STUDENT_CREDIT);
        when(savedOrder.getOrderDateTime()).thenReturn(java.time.LocalDateTime.now());

        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);

        // When
        PlaceOrderResponse response = useCase.execute(request);

        // Then
        assertNotNull(response, "La réponse ne devrait pas être nulle");
        assertNotNull(response.orderId(), "Un ID de commande devrait être généré");
        assertEquals("Marcel Dupont", response.customerName());
        assertEquals("Le Tacos du Campus", response.restaurantName());
        assertEquals(expectedTotal, response.totalAmount());
        assertEquals(OrderStatus.PENDING, response.status());
        assertEquals(PaymentMethod.STUDENT_CREDIT, response.paymentMethod());

        // Vérification de la déduction du crédit
        assertEquals(expectedRemainingCredit, testUser.getStudentCredit(),
            "Le crédit devrait être débité de 17€");

        // Vérification que le Cart a été supprimé
        verify(cartRepository).delete(testCart);

        // Vérification des interactions
        verify(userRepository).findById(userId);
        verify(restaurantRepository).findById(restaurantId);
        verify(userRepository).save(testUser);
        verify(orderRepository).save(any(Order.class));
    }

    /**
     * Test d'échec : crédit étudiant insuffisant.
     */
    @Test
    @DisplayName("Devrait échouer si le crédit étudiant est insuffisant")
    void should_fail_when_insufficient_student_credit() {
        // Given - Marcel a seulement 10€ mais le panier contient 17€
        testUser.setStudentCredit(BigDecimal.valueOf(10.00));

        PlaceOrderRequest request = new PlaceOrderRequest(
            userId,
            restaurantId,
            PaymentMethod.STUDENT_CREDIT
        );

        when(orderRepository.existsActiveOrderByUserId(userId)).thenReturn(false);
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(cartRepository.findActiveCartByUserId(userId)).thenReturn(Optional.of(testCart));
        when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.of(testRestaurant));

        // When & Then
        InsufficientCreditException exception = assertThrows(
            InsufficientCreditException.class,
            () -> useCase.execute(request),
            "Une exception devrait être levée pour crédit insuffisant"
        );

        assertTrue(exception.getMessage().contains("Crédit étudiant insuffisant"));
        assertEquals(BigDecimal.valueOf(10.00), testUser.getStudentCredit(),
            "Le crédit ne devrait pas avoir été débité");

        // Vérification qu'aucune commande n'a été sauvegardée
        verify(orderRepository, never()).save(any(Order.class));
        verify(userRepository, never()).save(any(User.class));
        verify(cartRepository, never()).delete(any(Cart.class));
    }

    /**
     * Test avec paiement par service externe.
     */
    @Test
    @DisplayName("Devrait transformer Cart en Order avec paiement externe sans débiter le crédit")
    void should_place_order_with_external_payment_without_deducting_credit() {
        // Given
        PlaceOrderRequest request = new PlaceOrderRequest(
            userId,
            restaurantId,
            PaymentMethod.EXTERNAL_CARD
        );

        BigDecimal initialCredit = testUser.getStudentCredit();

        when(orderRepository.existsActiveOrderByUserId(userId)).thenReturn(false);
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(cartRepository.findActiveCartByUserId(userId)).thenReturn(Optional.of(testCart));
        when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.of(testRestaurant));

        Order savedOrder = mock(Order.class);
        when(savedOrder.getOrderId()).thenReturn(UUID.randomUUID().toString());
        when(savedOrder.getUser()).thenReturn(testUser);
        when(savedOrder.getRestaurant()).thenReturn(testRestaurant);
        when(savedOrder.getTotalAmount()).thenReturn(BigDecimal.valueOf(17.0));
        when(savedOrder.getStatus()).thenReturn(OrderStatus.PENDING);
        when(savedOrder.getPaymentMethod()).thenReturn(PaymentMethod.EXTERNAL_CARD);
        when(savedOrder.getOrderDateTime()).thenReturn(java.time.LocalDateTime.now());

        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);

        // Mock PaymentStrategyFactory to return a strategy that always succeeds
        PaymentStrategy mockPaymentStrategy = mock(PaymentStrategy.class);
        when(mockPaymentStrategy.canPay(any(User.class), any(BigDecimal.class))).thenReturn(true);
        when(mockPaymentStrategy.processPayment(any(BigDecimal.class), any(User.class)))
            .thenReturn(PaymentResult.success("MOCK-TXN", BigDecimal.valueOf(17.0), "Paiement réussi"));

        try (MockedStatic<PaymentStrategyFactory> mockedFactory = mockStatic(PaymentStrategyFactory.class)) {
            mockedFactory.when(() -> PaymentStrategyFactory.createStrategy(PaymentMethod.EXTERNAL_CARD))
                .thenReturn(mockPaymentStrategy);

            // When
            PlaceOrderResponse response = useCase.execute(request);

            // Then
            assertNotNull(response);
            assertEquals(PaymentMethod.EXTERNAL_CARD, response.paymentMethod());
            assertEquals(initialCredit, testUser.getStudentCredit(),
                "Le crédit étudiant ne devrait PAS être débité pour paiement externe");

            // Vérification que le Cart a été supprimé
            verify(cartRepository).delete(testCart);

            // Vérification que l'utilisateur n'a pas été sauvegardé (pas de modification du crédit)
            verify(userRepository, never()).save(testUser);
            verify(orderRepository).save(any(Order.class));
        }
    }

    /**
     * Test d'échec : aucun panier actif.
     */
    @Test
    @DisplayName("Devrait échouer si aucun panier actif n'existe")
    void should_fail_when_no_active_cart() {
        // Given
        PlaceOrderRequest request = new PlaceOrderRequest(
            userId,
            restaurantId,
            PaymentMethod.STUDENT_CREDIT
        );

        when(orderRepository.existsActiveOrderByUserId(userId)).thenReturn(false);
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(cartRepository.findActiveCartByUserId(userId)).thenReturn(Optional.empty());

        // When & Then
        ValidationException exception = assertThrows(
            ValidationException.class,
            () -> useCase.execute(request),
            "Une exception devrait être levée si aucun panier actif"
        );

        assertTrue(exception.getMessage().contains("Aucun panier actif trouvé"));
        verify(orderRepository, never()).save(any());
    }

    /**
     * Test d'échec : panier vide.
     */
    @Test
    @DisplayName("Devrait échouer si le panier est vide")
    void should_fail_when_cart_is_empty() {
        // Given
        Cart emptyCart = new Cart(userId);

        PlaceOrderRequest request = new PlaceOrderRequest(
            userId,
            restaurantId,
            PaymentMethod.STUDENT_CREDIT
        );

        when(orderRepository.existsActiveOrderByUserId(userId)).thenReturn(false);
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(cartRepository.findActiveCartByUserId(userId)).thenReturn(Optional.of(emptyCart));

        // When & Then
        ValidationException exception = assertThrows(
            ValidationException.class,
            () -> useCase.execute(request),
            "Une exception devrait être levée si le panier est vide"
        );

        assertTrue(exception.getMessage().contains("Le panier est vide"));
        verify(orderRepository, never()).save(any());
    }

    /**
     * Test d'échec : utilisateur a déjà une commande active.
     */
    @Test
    @DisplayName("Devrait échouer si l'utilisateur a déjà une commande active")
    void should_fail_when_user_has_active_order() {
        // Given
        PlaceOrderRequest request = new PlaceOrderRequest(
            userId,
            restaurantId,
            PaymentMethod.STUDENT_CREDIT
        );

        // Mock : l'utilisateur a déjà une commande active
        when(orderRepository.existsActiveOrderByUserId(userId)).thenReturn(true);

        // When & Then
        ValidationException exception = assertThrows(
            ValidationException.class,
            () -> useCase.execute(request),
            "Une exception devrait être levée si une commande est déjà active"
        );

        assertTrue(exception.getMessage().contains("Vous avez déjà une commande en cours"));

        // Vérification qu'on n'a pas continué le traitement
        verify(userRepository, never()).findById(any());
        verify(cartRepository, never()).findActiveCartByUserId(any());
        verify(orderRepository, never()).save(any());
    }

    /**
     * Test d'échec : utilisateur introuvable.
     */
    @Test
    @DisplayName("Devrait échouer si l'utilisateur n'existe pas")
    void should_fail_when_user_not_found() {
        // Given
        PlaceOrderRequest request = new PlaceOrderRequest(
            userId,
            restaurantId,
            PaymentMethod.STUDENT_CREDIT
        );

        when(orderRepository.existsActiveOrderByUserId(userId)).thenReturn(false);
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // When & Then
        EntityNotFoundException exception = assertThrows(
            EntityNotFoundException.class,
            () -> useCase.execute(request),
            "Une exception devrait être levée si l'utilisateur n'existe pas"
        );

        assertTrue(exception.getMessage().contains("User not found"));
        verify(restaurantRepository, never()).findById(any());
        verify(orderRepository, never()).save(any());
    }

    /**
     * Test d'échec : restaurant introuvable.
     */
    @Test
    @DisplayName("Devrait échouer si le restaurant n'existe pas")
    void should_fail_when_restaurant_not_found() {
        // Given
        PlaceOrderRequest request = new PlaceOrderRequest(
            userId,
            restaurantId,
            PaymentMethod.STUDENT_CREDIT
        );

        when(orderRepository.existsActiveOrderByUserId(userId)).thenReturn(false);
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(cartRepository.findActiveCartByUserId(userId)).thenReturn(Optional.of(testCart));
        when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.empty());

        // When & Then
        EntityNotFoundException exception = assertThrows(
            EntityNotFoundException.class,
            () -> useCase.execute(request),
            "Une exception devrait être levée si le restaurant n'existe pas"
        );

        assertTrue(exception.getMessage().contains("Restaurant not found"));
        verify(orderRepository, never()).save(any());
    }

    /**
     * Test d'échec : requête invalide (null).
     */
    @Test
    @DisplayName("Devrait échouer si la requête est nulle")
    void should_fail_when_request_is_null() {
        // When & Then
        assertThrows(
            IllegalArgumentException.class,
            () -> useCase.execute(null),
            "Une exception devrait être levée si la requête est nulle"
        );

        verify(userRepository, never()).findById(any());
        verify(orderRepository, never()).save(any());
    }

    /**
     * Test de vérification que l'Order créé contient bien les bons items du Cart.
     */
    @Test
    @DisplayName("Devrait créer une Order avec les items du Cart")
    void should_create_order_with_items_from_cart() {
        // Given
        PlaceOrderRequest request = new PlaceOrderRequest(
            userId,
            restaurantId,
            PaymentMethod.STUDENT_CREDIT
        );

        when(orderRepository.existsActiveOrderByUserId(userId)).thenReturn(false);
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(cartRepository.findActiveCartByUserId(userId)).thenReturn(Optional.of(testCart));
        when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.of(testRestaurant));

        ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);

        Order savedOrder = mock(Order.class);
        when(savedOrder.getOrderId()).thenReturn(UUID.randomUUID().toString());
        when(savedOrder.getUser()).thenReturn(testUser);
        when(savedOrder.getRestaurant()).thenReturn(testRestaurant);
        when(savedOrder.getTotalAmount()).thenReturn(BigDecimal.valueOf(17.0));
        when(savedOrder.getStatus()).thenReturn(OrderStatus.PENDING);
        when(savedOrder.getPaymentMethod()).thenReturn(PaymentMethod.STUDENT_CREDIT);
        when(savedOrder.getOrderDateTime()).thenReturn(java.time.LocalDateTime.now());

        when(orderRepository.save(orderCaptor.capture())).thenReturn(savedOrder);

        // When
        useCase.execute(request);

        // Then
        Order capturedOrder = orderCaptor.getValue();
        assertNotNull(capturedOrder);
        assertEquals(testUser, capturedOrder.getUser());
        assertEquals(testRestaurant, capturedOrder.getRestaurant());
        assertEquals(PaymentMethod.STUDENT_CREDIT, capturedOrder.getPaymentMethod());
        assertEquals(1, capturedOrder.getOrderItems().size());

        // Vérifier que le crédit a été déduit
        assertEquals(BigDecimal.valueOf(33.0), testUser.getStudentCredit());

        // Vérifier que le Cart a été supprimé
        verify(cartRepository).delete(testCart);
    }
}
