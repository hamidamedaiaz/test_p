package fr.unice.polytech.sophiatecheats.domain.entities.order;

import fr.unice.polytech.sophiatecheats.domain.entities.restaurant.Dish;
import fr.unice.polytech.sophiatecheats.domain.entities.restaurant.Restaurant;
import fr.unice.polytech.sophiatecheats.domain.entities.user.User;
import fr.unice.polytech.sophiatecheats.domain.enums.DishCategory;
import fr.unice.polytech.sophiatecheats.domain.enums.OrderStatus;
import fr.unice.polytech.sophiatecheats.domain.enums.PaymentMethod;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class OrderConfirmationTest {

    private Order order;
    private User user;
    private Restaurant restaurant;

    @BeforeEach
    void setUp() {
        user = new User("john@example.com", "John Doe");
        user.setStudentCredit(BigDecimal.valueOf(50.0));
        restaurant = new Restaurant("Test Restaurant", "Test Address");

        Dish dish = Dish.builder()
                .name("Pizza")
                .description("Delicious pizza")
                .price(new BigDecimal("12.99"))
                .category(DishCategory.MAIN_COURSE)
                .build();

        OrderItem orderItem = new OrderItem(dish, 2);

        order = new Order(user, restaurant, List.of(orderItem), PaymentMethod.STUDENT_CREDIT);
    }

    @Test
    void testNewOrderStartsWithPendingStatus() {
        assertEquals(OrderStatus.PENDING, order.getStatus());
        assertNull(order.getDeliveryTime());
    }

    @Test
    void testConfirmOrderFromPendingStatus() {
        LocalDateTime beforeConfirm = LocalDateTime.now();

        order.confirm();

        assertEquals(OrderStatus.CONFIRMED, order.getStatus());
        assertNotNull(order.getDeliveryTime());
        assertTrue(order.getDeliveryTime().isAfter(beforeConfirm.plusMinutes(10)));
        assertTrue(order.getDeliveryTime().isBefore(beforeConfirm.plusMinutes(20)));
    }

    @Test
    void testConfirmOrderFromPaidStatus() {
        order.markAsPaid();
        assertEquals(OrderStatus.PAID, order.getStatus());

        order.confirm();

        assertEquals(OrderStatus.CONFIRMED, order.getStatus());
        assertNotNull(order.getDeliveryTime());
    }

    @Test
    void testCannotConfirmAlreadyConfirmedOrder() {
        order.confirm();

        IllegalStateException exception = assertThrows(IllegalStateException.class,
            () -> order.confirm());
        assertEquals("La commande est déjà confirmée", exception.getMessage());
    }

    @Test
    void testCannotConfirmExpiredOrder() {
        // Simuler une commande expirée en forçant le statut
        order.setStatus(OrderStatus.EXPIRED);

        IllegalStateException exception = assertThrows(IllegalStateException.class,
            () -> order.confirm());
        assertEquals("La commande a expiré et ne peut pas être confirmée", exception.getMessage());
    }

    @Test
    void testCanBeConfirmedReturnsTrueForPendingAndPaid() {
        // Test avec statut PENDING
        order.setStatus(OrderStatus.PENDING);
        assertTrue(order.canBeConfirmed());

        // Test avec statut PAID
        order.setStatus(OrderStatus.PAID);
        assertTrue(order.canBeConfirmed());
    }

    @Test
    void testCanBeConfirmedReturnsFalseForOtherStatuses() {
        // Test avec statut CONFIRMED
        order.setStatus(OrderStatus.CONFIRMED);
        assertFalse(order.canBeConfirmed());

        // Test avec statut EXPIRED
        order.setStatus(OrderStatus.EXPIRED);
        assertFalse(order.canBeConfirmed());
    }

    @Test
    void testMarkAsPaidSetsCorrectStatus() {
        order.markAsPaid();
        assertEquals(OrderStatus.PAID, order.getStatus());
    }

    @Test
    void testCannotMarkExpiredOrderAsPaid() {
        order.setStatus(OrderStatus.EXPIRED);

        IllegalStateException exception = assertThrows(IllegalStateException.class,
            () -> order.markAsPaid());
        assertEquals("La commande a expiré et ne peut pas être payée", exception.getMessage());
    }
}
