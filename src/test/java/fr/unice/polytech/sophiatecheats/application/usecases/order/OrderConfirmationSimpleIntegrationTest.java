package fr.unice.polytech.sophiatecheats.application.usecases.order;

import fr.unice.polytech.sophiatecheats.application.dto.order.ConfirmOrderRequest;
import fr.unice.polytech.sophiatecheats.application.dto.order.ConfirmOrderResponse;
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
import fr.unice.polytech.sophiatecheats.domain.repositories.OrderRepository;
import fr.unice.polytech.sophiatecheats.infrastructure.repositories.memory.InMemoryOrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test d'intégration simplifié pour la confirmation de commande
 */
class OrderConfirmationSimpleIntegrationTest {

    private ConfirmOrderUseCase confirmOrderUseCase;
    private OrderRepository orderRepository;

    @BeforeEach
    void setUp() {
        orderRepository = new InMemoryOrderRepository();
        confirmOrderUseCase = new ConfirmOrderUseCase(orderRepository);
    }

    @Test
    void testConfirmOrderSuccessfully() {
        // Given - Create a PAID order directly
        User user = new User("test@example.com", "Test User");
        user.setStudentCredit(BigDecimal.valueOf(50.0));

        Restaurant restaurant = new Restaurant("Test Restaurant", "Test Address");
        Dish pizza = Dish.builder()
            .id(UUID.randomUUID())
            .name("Pizza")
            .description("Delicious pizza")
            .price(BigDecimal.valueOf(15.0))
            .category(DishCategory.MAIN_COURSE)
            .available(true)
            .build();

        OrderItem orderItem = new OrderItem(pizza, 2);
        Order order = new Order(user, restaurant, List.of(orderItem), PaymentMethod.STUDENT_CREDIT);
        order.markAsPaid();

        Order savedOrder = orderRepository.save(order);

        // When - Confirm the order
        ConfirmOrderRequest request = new ConfirmOrderRequest(savedOrder.getOrderId());
        ConfirmOrderResponse response = confirmOrderUseCase.execute(request);

        // Then - Order should be confirmed
        assertNotNull(response);
        assertEquals(savedOrder.getOrderId(), response.orderId());
        assertEquals(OrderStatus.CONFIRMED, response.status());
        assertEquals(user.getName(), response.customerName());
        assertEquals(restaurant.getName(), response.restaurantName());
        assertEquals(BigDecimal.valueOf(30.0), response.totalAmount());
        assertNotNull(response.deliveryTime());
        assertNotNull(response.confirmedAt());
    }

    @Test
    void testCannotConfirmOrderTwice() {
        // Given - Create and confirm an order
        User user = new User("test@example.com", "Test User");
        Restaurant restaurant = new Restaurant("Test Restaurant", "Test Address");
        Dish pizza = Dish.builder()
            .id(UUID.randomUUID())
            .name("Pizza")
            .description("Delicious pizza")
            .price(BigDecimal.valueOf(15.0))
            .category(DishCategory.MAIN_COURSE)
            .available(true)
            .build();

        OrderItem orderItem = new OrderItem(pizza, 1);
        Order order = new Order(user, restaurant, List.of(orderItem), PaymentMethod.STUDENT_CREDIT);
        order.markAsPaid();

        Order savedOrder = orderRepository.save(order);

        // Confirm once
        ConfirmOrderRequest request = new ConfirmOrderRequest(savedOrder.getOrderId());
        confirmOrderUseCase.execute(request);

        // When & Then - Attempting to confirm again should fail
        assertThrows(OrderAlreadyConfirmedException.class,
            () -> confirmOrderUseCase.execute(request));
    }

    @Test
    void testConfirmNonExistentOrder() {
        // When & Then
        ConfirmOrderRequest request = new ConfirmOrderRequest("non-existent-order-id");
        assertThrows(EntityNotFoundException.class,
            () -> confirmOrderUseCase.execute(request));
    }
}
