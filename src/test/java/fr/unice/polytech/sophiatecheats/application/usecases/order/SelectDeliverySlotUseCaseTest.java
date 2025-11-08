package fr.unice.polytech.sophiatecheats.application.usecases.order;

import fr.unice.polytech.sophiatecheats.application.dto.order.request.SelectDeliverySlotRequest;
import fr.unice.polytech.sophiatecheats.domain.entities.order.Order;
import fr.unice.polytech.sophiatecheats.domain.enums.OrderStatus;
import fr.unice.polytech.sophiatecheats.domain.exceptions.ValidationException;
import fr.unice.polytech.sophiatecheats.domain.repositories.OrderRepository;
import fr.unice.polytech.sophiatecheats.domain.repositories.RestaurantRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("SelectDeliverySlotUseCase - Validation commande avant créneau")
class SelectDeliverySlotUseCaseTest {
    private OrderRepository orderRepository;
    private RestaurantRepository restaurantRepository;
    private SelectDeliverySlotUseCase useCase;

    @BeforeEach
    void setUp() {
        orderRepository = mock(OrderRepository.class);
        restaurantRepository = mock(RestaurantRepository.class);
        useCase = new SelectDeliverySlotUseCase(orderRepository, restaurantRepository);
    }

    @Test
    @DisplayName("Impossible de réserver un créneau pour une commande non validée")
    void cannotReserveSlotForNonValidatedOrder() {
        // Prépare une commande avec un statut non valide (ex: EXPIRED)
        Order order = mock(Order.class);
        when(order.getStatus()).thenReturn(OrderStatus.EXPIRED);
        when(order.hasDeliverySlot()).thenReturn(false);
        String orderId = UUID.randomUUID().toString();
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        SelectDeliverySlotRequest request = mock(SelectDeliverySlotRequest.class);
        when(request.orderId()).thenReturn(orderId);
        when(request.isValid()).thenReturn(true);
        ValidationException ex = assertThrows(ValidationException.class, () -> useCase.execute(request));
        assertTrue(ex.getMessage().contains("Impossible de réserver un créneau pour une commande non validée"));
    }
}
