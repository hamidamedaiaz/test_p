package fr.unice.polytech.sophiatecheats.domain.entities.order;

import fr.unice.polytech.sophiatecheats.domain.entities.restaurant.Dish;
import fr.unice.polytech.sophiatecheats.domain.entities.restaurant.Restaurant;
import fr.unice.polytech.sophiatecheats.domain.entities.user.User;
import fr.unice.polytech.sophiatecheats.domain.enums.DishCategory;
import fr.unice.polytech.sophiatecheats.domain.enums.OrderStatus;
import fr.unice.polytech.sophiatecheats.domain.enums.PaymentMethod;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class OrderTest {

    @Test
    void shouldCreateOrderWithCorrectTotalAmount() {

        User user = new User("test@example.com", "John Doe");
        Restaurant restaurant = new Restaurant("Test Restaurant", "123 Main St");
        Dish dish = new Dish(UUID.randomUUID(), "Tacos", "Delicious tacos", BigDecimal.valueOf(8.50), DishCategory.MAIN_COURSE, true);
        OrderItem item = new OrderItem(dish, 2);
        List<OrderItem> items = List.of(item);


        Order order = new Order(user, restaurant, items, PaymentMethod.STUDENT_CREDIT);
        assertNotNull(order.getOrderId());
        assertEquals(user, order.getUser());
        assertEquals(restaurant, order.getRestaurant());
        assertEquals(OrderStatus.PENDING, order.getStatus());
        assertEquals(BigDecimal.valueOf(17.00), order.getTotalAmount());
        assertEquals(PaymentMethod.STUDENT_CREDIT, order.getPaymentMethod());
        assertNotNull(order.getOrderDateTime());
    }

    @Test
    void shouldCalculateTotalAmountCorrectly() {
        User user = new User("test@example.com", "John Doe");
        Restaurant restaurant = new Restaurant("Test Restaurant", "123 Main St");

        Dish dish1 = new Dish(UUID.randomUUID(), "Tacos", "Delicious tacos", BigDecimal.valueOf(8.50), DishCategory.MAIN_COURSE, true);
        Dish dish2 = new Dish(UUID.randomUUID(), "Burger", "Tasty burger", BigDecimal.valueOf(12.00), DishCategory.MAIN_COURSE, true);

        OrderItem item1 = new OrderItem(dish1, 2);
        OrderItem item2 = new OrderItem(dish2, 1);

        List<OrderItem> items = List.of(item1, item2);

        Order order = new Order(user, restaurant, items, PaymentMethod.EXTERNAL_CARD);

        assertEquals(BigDecimal.valueOf(29.00), order.getTotalAmount());
    }
}
