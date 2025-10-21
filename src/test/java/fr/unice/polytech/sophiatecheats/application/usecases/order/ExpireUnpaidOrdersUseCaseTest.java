package fr.unice.polytech.sophiatecheats.application.usecases.order;

import fr.unice.polytech.sophiatecheats.domain.entities.order.Order;
import fr.unice.polytech.sophiatecheats.domain.enums.OrderStatus;
import fr.unice.polytech.sophiatecheats.domain.repositories.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("ExpireUnpaidOrdersUseCase - Timeout paiement et libération créneau")
class ExpireUnpaidOrdersUseCaseTest {
    private OrderRepository orderRepository;
    private ExpireUnpaidOrdersUseCase useCase;

    @BeforeEach
    void setUp() {
        orderRepository = mock(OrderRepository.class);
        useCase = new ExpireUnpaidOrdersUseCase(orderRepository, Duration.ofMinutes(5));
    }

    @Test
    @DisplayName("Expire les commandes non payées après 5 minutes et libère le créneau")
    void expireUnpaidOrderAndReleaseSlot() {
        Order expiredOrder = mock(Order.class);
        when(expiredOrder.hasDeliverySlot()).thenReturn(true);
        when(expiredOrder.getDeliverySlotReservedAt()).thenReturn(LocalDateTime.now().minusMinutes(6));
        when(expiredOrder.getStatus()).thenReturn(OrderStatus.PENDING);
        Order freshOrder = mock(Order.class);
        when(freshOrder.hasDeliverySlot()).thenReturn(true);
        when(freshOrder.getDeliverySlotReservedAt()).thenReturn(LocalDateTime.now().minusMinutes(2));
        when(freshOrder.getStatus()).thenReturn(OrderStatus.PENDING);
        when(orderRepository.findAllByStatus(OrderStatus.PENDING)).thenReturn(Arrays.asList(expiredOrder, freshOrder));
        useCase.run();
        verify(expiredOrder).expire();
        verify(orderRepository).save(expiredOrder);
        verify(freshOrder, never()).expire();
        verify(orderRepository, never()).save(freshOrder);
    }
}

