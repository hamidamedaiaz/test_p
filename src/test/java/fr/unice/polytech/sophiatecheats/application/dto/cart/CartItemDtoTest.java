package fr.unice.polytech.sophiatecheats.application.dto.cart;

import fr.unice.polytech.sophiatecheats.application.dto.user.CartItemDto;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

 class CartItemDtoTest {

    @Test
    void should_create_cart_item_dto_with_valid_data() {
        UUID dishId = UUID.randomUUID();
        CartItemDto dto = new CartItemDto(
            dishId,
            "Pizza Margherita",
            "Classic Italian pizza",
            new BigDecimal("12.50"),
            2,
            new BigDecimal("25.00")
        );

        assertEquals(dishId, dto.dishId());
        assertEquals("Pizza Margherita", dto.dishName());
        assertEquals("Classic Italian pizza", dto.dishDescription());
        assertEquals(new BigDecimal("12.50"), dto.unitPrice());
        assertEquals(2, dto.quantity());
        assertEquals(new BigDecimal("25.00"), dto.subtotal());
    }

    @Test
    void should_create_cart_item_dto_with_single_quantity() {
        UUID dishId = UUID.randomUUID();
        CartItemDto dto = new CartItemDto(
            dishId,
            "Burger",
            "Delicious burger",
            new BigDecimal("8.00"),
            1,
            new BigDecimal("8.00")
        );

        assertEquals(1, dto.quantity());
        assertEquals(new BigDecimal("8.00"), dto.subtotal());
    }

    @Test
    void should_handle_null_description() {
        UUID dishId = UUID.randomUUID();
        CartItemDto dto = new CartItemDto(
            dishId,
            "Salad",
            null,
            new BigDecimal("7.00"),
            1,
            new BigDecimal("7.00")
        );

        assertNull(dto.dishDescription());
        assertEquals("Salad", dto.dishName());
    }

    @Test
    void should_create_cart_item_with_large_quantity() {
        UUID dishId = UUID.randomUUID();
        CartItemDto dto = new CartItemDto(
            dishId,
            "French Fries",
            "Crispy fries",
            new BigDecimal("3.50"),
            10,
            new BigDecimal("35.00")
        );

        assertEquals(10, dto.quantity());
        assertEquals(new BigDecimal("35.00"), dto.subtotal());
    }

    @Test
    void should_handle_zero_values() {
        UUID dishId = UUID.randomUUID();
        CartItemDto dto = new CartItemDto(
            dishId,
            "Test Item",
            "Test",
            BigDecimal.ZERO,
            0,
            BigDecimal.ZERO
        );

        assertEquals(BigDecimal.ZERO, dto.unitPrice());
        assertEquals(0, dto.quantity());
        assertEquals(BigDecimal.ZERO, dto.subtotal());
    }
}

