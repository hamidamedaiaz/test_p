package fr.unice.polytech.sophiatecheats.application.usecases.order;

import fr.unice.polytech.sophiatecheats.domain.entities.order.Order;
import fr.unice.polytech.sophiatecheats.domain.entities.order.OrderItem;
import fr.unice.polytech.sophiatecheats.domain.entities.restaurant.Restaurant;
import fr.unice.polytech.sophiatecheats.domain.entities.user.User;
import fr.unice.polytech.sophiatecheats.domain.enums.OrderStatus;
import fr.unice.polytech.sophiatecheats.domain.enums.PaymentMethod;
import fr.unice.polytech.sophiatecheats.domain.repositories.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("ExpireOrderUseCase - Expiration et libération du créneau")
class ExpireOrderUseCaseTest {
    private OrderRepository orderRepository;
    private ExpireOrderUseCase useCase;

    @BeforeEach
    void setUp() {
        orderRepository = mock(OrderRepository.class);
        useCase = new ExpireOrderUseCase(orderRepository);
    }

    @Test
    @DisplayName("Expire la commande et libère le créneau de livraison")
    void expireOrderAndReleaseSlot() {
        // Création d'une commande avec un créneau
        User user = mock(User.class);
        Restaurant restaurant = mock(Restaurant.class);
        Order order = new Order(user, restaurant, Collections.emptyList(), PaymentMethod.EXTERNAL_CARD);
        UUID slotId = UUID.randomUUID();
        order.assignDeliverySlot(slotId, LocalDateTime.now().plusHours(1));
        String orderId = order.getOrderId();
        assertEquals(slotId, order.getDeliverySlotId());
        assertNotEquals(OrderStatus.EXPIRED, order.getStatus());
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(orderRepository.save(order)).thenReturn(order);
        // Appel du use case
        useCase.execute(orderId);
        // Vérifications
        assertEquals(OrderStatus.EXPIRED, order.getStatus());
        assertNull(order.getDeliverySlotId(), "Le créneau doit être libéré");
        verify(orderRepository).save(order);
    }
}

