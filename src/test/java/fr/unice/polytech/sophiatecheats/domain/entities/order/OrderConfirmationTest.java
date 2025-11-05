package fr.unice.polytech.sophiatecheats.domain.entities.order;

import fr.unice.polytech.sophiatecheats.domain.entities.restaurant.Dish;
import fr.unice.polytech.sophiatecheats.domain.enums.DishCategory;
import fr.unice.polytech.sophiatecheats.domain.exceptions.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class OrderConfirmationTest {

    private UUID orderId;
    private UUID userId;
    private String userEmail;
    private List<OrderItem> orderItems;
    private BigDecimal totalAmount;
    private LocalDateTime estimatedDeliveryTime;
    private String deliveryLocation;

    @BeforeEach
    void setUp() {
        orderId = UUID.randomUUID();
        userId = UUID.randomUUID();
        userEmail = "user@example.com";

        // Créer des items de test
        Dish dish1 = new Dish(UUID.randomUUID(), "Pizza Margherita", "Pizza classique",
                             new BigDecimal("12.50"), DishCategory.MAIN_COURSE, true);
        Dish dish2 = new Dish(UUID.randomUUID(), "Salade César", "Salade fraîche",
                             new BigDecimal("8.50"), DishCategory.STARTER, true);

        orderItems = List.of(
            new OrderItem(dish1, 2),
            new OrderItem(dish2, 1)
        );

        totalAmount = new BigDecimal("33.50");
        estimatedDeliveryTime = LocalDateTime.now().plusHours(1);
        deliveryLocation = "Campus Polytech - Bâtiment A";
    }

    @Test
    void shouldCreateOrderConfirmationWithValidData() {
        // When
        OrderConfirmation confirmation = new OrderConfirmation(
            orderId, userId, userEmail, orderItems, totalAmount,
            estimatedDeliveryTime, deliveryLocation
        );

        // Then
        assertNotNull(confirmation.getId());
        assertEquals(orderId, confirmation.getOrderId());
        assertEquals(userId, confirmation.getUserId());
        assertEquals(userEmail, confirmation.getUserEmail());
        assertEquals(orderItems.size(), confirmation.getItems().size());
        assertEquals(totalAmount, confirmation.getTotalAmount());
        assertEquals(estimatedDeliveryTime, confirmation.getEstimatedDeliveryTime());
        assertEquals(deliveryLocation, confirmation.getDeliveryLocation());
        assertNotNull(confirmation.getConfirmationNumber());
        assertNotNull(confirmation.getCreatedAt());
        assertTrue(confirmation.getConfirmationNumber().startsWith("STE-"));
    }

    @Test
    void shouldGenerateUniqueConfirmationNumbers() {
        // When
        OrderConfirmation confirmation1 = new OrderConfirmation(
            orderId, userId, userEmail, orderItems, totalAmount,
            estimatedDeliveryTime, deliveryLocation
        );

        OrderConfirmation confirmation2 = new OrderConfirmation(
            UUID.randomUUID(), userId, userEmail, orderItems, totalAmount,
            estimatedDeliveryTime, deliveryLocation
        );

        // Then
        assertNotEquals(confirmation1.getConfirmationNumber(), confirmation2.getConfirmationNumber());
    }

    @Test
    void shouldThrowExceptionWhenOrderIdIsNull() {
        // When & Then
        ValidationException exception = assertThrows(ValidationException.class, () ->
            new OrderConfirmation(null, userId, userEmail, orderItems, totalAmount,
                                estimatedDeliveryTime, deliveryLocation)
        );
        assertEquals("L'ID de commande ne peut pas être null", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenUserIdIsNull() {
        // When & Then
        ValidationException exception = assertThrows(ValidationException.class, () ->
            new OrderConfirmation(orderId, null, userEmail, orderItems, totalAmount,
                                estimatedDeliveryTime, deliveryLocation)
        );
        assertEquals("L'ID utilisateur ne peut pas être null", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenUserEmailIsNull() {
        // When & Then
        ValidationException exception = assertThrows(ValidationException.class, () ->
            new OrderConfirmation(orderId, userId, null, orderItems, totalAmount,
                                estimatedDeliveryTime, deliveryLocation)
        );
        assertEquals("L'email utilisateur ne peut pas être vide", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenUserEmailIsEmpty() {
        // When & Then
        ValidationException exception = assertThrows(ValidationException.class, () ->
            new OrderConfirmation(orderId, userId, "   ", orderItems, totalAmount,
                                estimatedDeliveryTime, deliveryLocation)
        );
        assertEquals("L'email utilisateur ne peut pas être vide", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenUserEmailHasInvalidFormat() {
        // When & Then
        ValidationException exception = assertThrows(ValidationException.class, () ->
            new OrderConfirmation(orderId, userId, "invalid-email", orderItems, totalAmount,
                                estimatedDeliveryTime, deliveryLocation)
        );
        assertEquals("Format d'email invalide: invalid-email", exception.getMessage());
    }

    @Test
    void shouldAcceptValidEmailFormats() {
        // Given
        String[] validEmails = {
            "user@example.com",
            "user.name@example.com",
            "user+tag@example.com",
            "user123@example.org",
            "test@sub.example.com"
        };

        // When & Then
        for (String email : validEmails) {
            assertDoesNotThrow(() ->
                new OrderConfirmation(orderId, userId, email, orderItems, totalAmount,
                                    estimatedDeliveryTime, deliveryLocation)
            );
        }
    }

    @Test
    void shouldThrowExceptionWhenItemsListIsNull() {
        // When & Then
        ValidationException exception = assertThrows(ValidationException.class, () ->
            new OrderConfirmation(orderId, userId, userEmail, null, totalAmount,
                                estimatedDeliveryTime, deliveryLocation)
        );
        assertEquals("La liste des articles ne peut pas être vide", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenItemsListIsEmpty() {
        // When & Then
        ValidationException exception = assertThrows(ValidationException.class, () ->
            new OrderConfirmation(orderId, userId, userEmail, new ArrayList<>(), totalAmount,
                                estimatedDeliveryTime, deliveryLocation)
        );
        assertEquals("La liste des articles ne peut pas être vide", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenTotalAmountIsNull() {
        // When & Then
        ValidationException exception = assertThrows(ValidationException.class, () ->
            new OrderConfirmation(orderId, userId, userEmail, orderItems, null,
                                estimatedDeliveryTime, deliveryLocation)
        );
        assertEquals("Le montant total doit être positif", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenTotalAmountIsNegative() {
        // When & Then
        ValidationException exception = assertThrows(ValidationException.class, () ->
            new OrderConfirmation(orderId, userId, userEmail, orderItems, new BigDecimal("-10.00"),
                                estimatedDeliveryTime, deliveryLocation)
        );
        assertEquals("Le montant total doit être positif", exception.getMessage());
    }

    @Test
    void shouldAcceptZeroTotalAmount() {
        // When & Then
        assertDoesNotThrow(() ->
            new OrderConfirmation(orderId, userId, userEmail, orderItems, BigDecimal.ZERO,
                                estimatedDeliveryTime, deliveryLocation)
        );
    }

    @Test
    void shouldThrowExceptionWhenEstimatedDeliveryTimeIsNull() {
        // When & Then
        ValidationException exception = assertThrows(ValidationException.class, () ->
            new OrderConfirmation(orderId, userId, userEmail, orderItems, totalAmount,
                                null, deliveryLocation)
        );
        assertEquals("L'heure de livraison estimée ne peut pas être null", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenDeliveryLocationIsNull() {
        // When & Then
        ValidationException exception = assertThrows(ValidationException.class, () ->
            new OrderConfirmation(orderId, userId, userEmail, orderItems, totalAmount,
                                estimatedDeliveryTime, null)
        );
        assertEquals("Le lieu de livraison ne peut pas être vide", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenDeliveryLocationIsEmpty() {
        // When & Then
        ValidationException exception = assertThrows(ValidationException.class, () ->
            new OrderConfirmation(orderId, userId, userEmail, orderItems, totalAmount,
                                estimatedDeliveryTime, "   ")
        );
        assertEquals("Le lieu de livraison ne peut pas être vide", exception.getMessage());
    }

    @Test
    void shouldReturnImmutableCopyOfItems() {
        // Given
        OrderConfirmation confirmation = new OrderConfirmation(
            orderId, userId, userEmail, orderItems, totalAmount,
            estimatedDeliveryTime, deliveryLocation
        );

        // When
        List<OrderItem> items = confirmation.getItems();

        // Then
        assertThrows(UnsupportedOperationException.class, () ->
            items.add(new OrderItem(
                new Dish(UUID.randomUUID(), "Test", "Test", BigDecimal.ONE, DishCategory.DESSERT, true),
                1
            ))
        );
    }



    @Test
    void shouldValidateEmailWithComplexDomains() {
        // Given
        String complexEmail = "user@subdomain.example.co.uk";

        // When & Then
        assertDoesNotThrow(() ->
            new OrderConfirmation(orderId, userId, complexEmail, orderItems, totalAmount,
                                estimatedDeliveryTime, deliveryLocation)
        );
    }
}
