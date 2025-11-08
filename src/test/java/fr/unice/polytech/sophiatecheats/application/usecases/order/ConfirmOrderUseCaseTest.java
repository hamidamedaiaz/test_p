package fr.unice.polytech.sophiatecheats.application.usecases.order;

import fr.unice.polytech.sophiatecheats.application.dto.order.request.ConfirmOrderRequest;
import fr.unice.polytech.sophiatecheats.application.dto.order.response.ConfirmOrderResponse;
import fr.unice.polytech.sophiatecheats.domain.entities.order.Order;
import fr.unice.polytech.sophiatecheats.domain.entities.order.OrderItem;
import fr.unice.polytech.sophiatecheats.domain.entities.restaurant.Dish;
import fr.unice.polytech.sophiatecheats.domain.entities.restaurant.Restaurant;
import fr.unice.polytech.sophiatecheats.domain.entities.user.User;
import fr.unice.polytech.sophiatecheats.domain.enums.DishCategory;
import fr.unice.polytech.sophiatecheats.domain.enums.OrderStatus;
import fr.unice.polytech.sophiatecheats.domain.enums.PaymentMethod;
import fr.unice.polytech.sophiatecheats.domain.exceptions.EntityNotFoundException;
import fr.unice.polytech.sophiatecheats.domain.exceptions.OrderAlreadyConfirmedException;
import fr.unice.polytech.sophiatecheats.domain.exceptions.OrderExpiredException;
import fr.unice.polytech.sophiatecheats.domain.repositories.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ConfirmOrderUseCaseTest {

    @Mock
    private OrderRepository orderRepository;

    private ConfirmOrderUseCase useCase;
    private Order testOrder;
    private User testUser;
    private Restaurant testRestaurant;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        useCase = new ConfirmOrderUseCase(orderRepository);

        // Setup test data - corriger le constructeur User
        testUser = new User("john@example.com", "John Doe");
        testUser.setStudentCredit(BigDecimal.valueOf(50.0));
        testRestaurant = new Restaurant("Test Restaurant", "Test Address");

        Dish dish = Dish.builder()
                .name("Pizza")
                .description("Delicious pizza")
                .price(new BigDecimal("12.99"))
                .category(DishCategory.MAIN_COURSE)
                .build();

        OrderItem orderItem = new OrderItem(dish, 2);

        testOrder = new Order(testUser, testRestaurant, List.of(orderItem), PaymentMethod.STUDENT_CREDIT);
        testOrder.markAsPaid(); // Set to PAID status for confirmation
    }

    @Test
    void testConfirmOrderSuccessfully() {
        // Given
        ConfirmOrderRequest request = new ConfirmOrderRequest(testOrder.getOrderId());
        when(orderRepository.findById(testOrder.getOrderId())).thenReturn(Optional.of(testOrder));
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);

        // When
        ConfirmOrderResponse response = useCase.execute(request);

        // Then
        assertNotNull(response);
        assertEquals(testOrder.getOrderId(), response.orderId());
        assertEquals(testUser.getName(), response.customerName());
        assertEquals(testRestaurant.getName(), response.restaurantName());
        assertEquals(testOrder.getTotalAmount(), response.totalAmount());
        assertEquals(OrderStatus.CONFIRMED, response.status());
        assertNotNull(response.deliveryTime());

        verify(orderRepository).findById(testOrder.getOrderId());
        verify(orderRepository).save(testOrder);
        assertEquals(OrderStatus.CONFIRMED, testOrder.getStatus());
    }

    @Test
    void testConfirmOrderWithInvalidRequest() {
        // Test with null request
        IllegalArgumentException exception1 = assertThrows(IllegalArgumentException.class,
            () -> useCase.execute(null));
        assertEquals("Invalid request", exception1.getMessage());

        // Test with invalid request (null orderId)
        ConfirmOrderRequest invalidRequest = new ConfirmOrderRequest(null);
        IllegalArgumentException exception2 = assertThrows(IllegalArgumentException.class,
            () -> useCase.execute(invalidRequest));
        assertEquals("Invalid request", exception2.getMessage());

        // Test with invalid request (empty orderId)
        ConfirmOrderRequest emptyRequest = new ConfirmOrderRequest("  ");
        IllegalArgumentException exception3 = assertThrows(IllegalArgumentException.class,
            () -> useCase.execute(emptyRequest));
        assertEquals("Invalid request", exception3.getMessage());
    }

    @Test
    void testConfirmOrderNotFound() {
        // Given
        String nonExistentOrderId = "non-existent-order";
        ConfirmOrderRequest request = new ConfirmOrderRequest(nonExistentOrderId);
        when(orderRepository.findById(nonExistentOrderId)).thenReturn(Optional.empty());

        // When & Then
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
            () -> useCase.execute(request));
        assertEquals("Order not found: " + nonExistentOrderId, exception.getMessage());

        verify(orderRepository).findById(nonExistentOrderId);
        verify(orderRepository, never()).save(any());
    }

    @Test
    void testConfirmAlreadyConfirmedOrder() {
        // Given
        testOrder.confirm(); // Already confirmed
        ConfirmOrderRequest request = new ConfirmOrderRequest(testOrder.getOrderId());
        when(orderRepository.findById(testOrder.getOrderId())).thenReturn(Optional.of(testOrder));

        // When & Then
        OrderAlreadyConfirmedException exception = assertThrows(OrderAlreadyConfirmedException.class,
            () -> useCase.execute(request));
        assertEquals("La commande " + testOrder.getOrderId() + " est déjà confirmée", exception.getMessage());

        verify(orderRepository).findById(testOrder.getOrderId());
        verify(orderRepository, never()).save(any());
    }

    @Test
    void testConfirmExpiredOrder() {
        // Given
        testOrder.setStatus(OrderStatus.EXPIRED);
        ConfirmOrderRequest request = new ConfirmOrderRequest(testOrder.getOrderId());
        when(orderRepository.findById(testOrder.getOrderId())).thenReturn(Optional.of(testOrder));

        // When & Then
        OrderExpiredException exception = assertThrows(OrderExpiredException.class,
            () -> useCase.execute(request));
        assertEquals("La commande " + testOrder.getOrderId() + " a expiré et ne peut plus être modifiée", exception.getMessage());

        verify(orderRepository).findById(testOrder.getOrderId());
        verify(orderRepository, never()).save(any());
    }

    @Test
    void testConfirmOrderFromPendingStatus() {
        // Given
        testOrder.setStatus(OrderStatus.PENDING); // Reset to PENDING
        ConfirmOrderRequest request = new ConfirmOrderRequest(testOrder.getOrderId());
        when(orderRepository.findById(testOrder.getOrderId())).thenReturn(Optional.of(testOrder));
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);

        // When
        ConfirmOrderResponse response = useCase.execute(request);

        // Then
        assertEquals(OrderStatus.CONFIRMED, response.status());
        assertEquals(OrderStatus.CONFIRMED, testOrder.getStatus());
        assertNotNull(testOrder.getDeliveryTime());
    }

    @Test
    void testConfirmOrderInInvalidStatus() {
        // Given - Order with CREATED status (invalid for confirmation)
        testOrder.setStatus(OrderStatus.CREATED);
        ConfirmOrderRequest request = new ConfirmOrderRequest(testOrder.getOrderId());
        when(orderRepository.findById(testOrder.getOrderId())).thenReturn(Optional.of(testOrder));

        // When & Then
        IllegalStateException exception = assertThrows(IllegalStateException.class,
            () -> useCase.execute(request));
        assertEquals("Cannot confirm order in status: CREATED", exception.getMessage());

        verify(orderRepository).findById(testOrder.getOrderId());
        verify(orderRepository, never()).save(any());
    }
}
