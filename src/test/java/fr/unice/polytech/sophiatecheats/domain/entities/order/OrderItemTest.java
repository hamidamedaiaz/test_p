package fr.unice.polytech.sophiatecheats.domain.entities.order;

import fr.unice.polytech.sophiatecheats.domain.entities.restaurant.Dish;
import fr.unice.polytech.sophiatecheats.domain.enums.DishCategory;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class OrderItemTest {
   Dish  dish  = Dish.builder()
           .name("Tacos")
           .description("Delicious tacos")
           .price(BigDecimal.valueOf(8.50))
           .category(DishCategory.DESSERT)
           .available(true)
           .build();


    @Test
    void should_create_orderItem_and_calculate_totalPrice() {
          dish.setPrice(BigDecimal.valueOf(5.50));
        OrderItem item = new OrderItem(dish, 3);
        assertEquals(dish, item.getDish());
        assertEquals(3, item.getQuantity());
        assertEquals(BigDecimal.valueOf(5.50), item.getUnitPrice());
        assertEquals(BigDecimal.valueOf(16.50), item.getTotalPrice());
    }

    @Test
    void should_return_zero_totalPrice_for_zero_quantity() {
        dish.setPrice(BigDecimal.valueOf(10));
 OrderItem item = new OrderItem(dish, 0);
        assertEquals(BigDecimal.ZERO, item.getTotalPrice());
    }

    @Test
    void should_handle_negative_quantity() {
        dish.setPrice(BigDecimal.valueOf(7));
        OrderItem item = new OrderItem(dish, -2);
        assertEquals(BigDecimal.valueOf(-14), item.getTotalPrice());
    }

    @Test
    void should_throw_NullPointerException_if_dish_is_null() {
        assertThrows(NullPointerException.class, () -> new OrderItem(null, 1));
    }

    @Test
    void should_throw_NullPointerException_if_dish_price_is_null() {
        dish.setPrice(null);

        assertThrows(NullPointerException.class, () -> new OrderItem(dish, 1));
    }
}
