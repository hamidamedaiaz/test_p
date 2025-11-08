package fr.unice.polytech.sophiatecheats.application.usecases.order;

import fr.unice.polytech.sophiatecheats.domain.entities.order.Order;
import fr.unice.polytech.sophiatecheats.domain.enums.OrderStatus;
import fr.unice.polytech.sophiatecheats.domain.exceptions.EntityNotFoundException;
import fr.unice.polytech.sophiatecheats.domain.exceptions.ValidationException;
import fr.unice.polytech.sophiatecheats.domain.repositories.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("InitiatePaymentUseCase - Démarrage du timeout paiement")
class InitiatePaymentUseCaseTest {
    private OrderRepository orderRepository;
    private InitiatePaymentUseCase useCase;

    @BeforeEach
    void setUp() {
        orderRepository = mock(OrderRepository.class);
        useCase = new InitiatePaymentUseCase(orderRepository);
    }

    @Test
    @DisplayName("Démarre le timeout paiement si commande PENDING avec créneau")
    void startTimeoutIfPendingWithSlot() {
        Order order = mock(Order.class);
        String orderId = UUID.randomUUID().toString();
        when(order.hasDeliverySlot()).thenReturn(true);
        when(order.getStatus()).thenReturn(OrderStatus.PENDING);
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        useCase.execute(orderId);
        verify(order).startPaymentTimeout();
        verify(orderRepository).save(order);
    }

    @Test
    @DisplayName("Refuse si commande déjà payée")
    void refuseIfAlreadyPaid() {
        Order order = mock(Order.class);
        String orderId = UUID.randomUUID().toString();
        when(order.hasDeliverySlot()).thenReturn(true);
        when(order.getStatus()).thenReturn(OrderStatus.PAID);
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        assertThrows(ValidationException.class, () -> useCase.execute(orderId));
        verify(order, never()).startPaymentTimeout();
    }

    @Test
    @DisplayName("Refuse si commande expirée")
    void refuseIfExpired() {
        Order order = mock(Order.class);
        String orderId = UUID.randomUUID().toString();
        when(order.hasDeliverySlot()).thenReturn(true);
        when(order.getStatus()).thenReturn(OrderStatus.EXPIRED);
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        assertThrows(ValidationException.class, () -> useCase.execute(orderId));
        verify(order, never()).startPaymentTimeout();
    }

    @Test
    @DisplayName("Refuse si pas de créneau")
    void refuseIfNoSlot() {
        Order order = mock(Order.class);
        String orderId = UUID.randomUUID().toString();
        when(order.hasDeliverySlot()).thenReturn(false);
        when(order.getStatus()).thenReturn(OrderStatus.PENDING);
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        assertThrows(ValidationException.class, () -> useCase.execute(orderId));
        verify(order, never()).startPaymentTimeout();
    }

    @Test
    @DisplayName("Refuse si commande inconnue")
    void refuseIfOrderNotFound() {
        String orderId = UUID.randomUUID().toString();
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> useCase.execute(orderId));
    }
}

