package fr.unice.polytech.sophiatecheats.application.usecases.delivery;

import fr.unice.polytech.sophiatecheats.application.usecases.user.delivery.GetAvailableDeliverySlotsUseCase;
import fr.unice.polytech.sophiatecheats.application.usecases.user.delivery.SelectDeliverySlotUseCase;
import fr.unice.polytech.sophiatecheats.application.usecases.user.delivery.ValidateDeliverySlotUseCase;
import fr.unice.polytech.sophiatecheats.domain.entities.restaurant.TimeSlot;
import fr.unice.polytech.sophiatecheats.domain.exceptions.SlotNotFoundException;
import fr.unice.polytech.sophiatecheats.domain.services.DeliveryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests pour les cas d’utilisation concernant les créneaux de livraison.
 */
class TimeSlotUseCasesTest {

    private DeliveryService deliveryService;
    private GetAvailableDeliverySlotsUseCase getAvailableUC;
    private SelectDeliverySlotUseCase selectSlotUC;
    private ValidateDeliverySlotUseCase validateSlotUC;
    private UUID restaurantId;

    @BeforeEach
    void setUp() {
        deliveryService = Mockito.mock(DeliveryService.class);
        getAvailableUC = new GetAvailableDeliverySlotsUseCase(deliveryService);
        selectSlotUC = new SelectDeliverySlotUseCase(deliveryService);
        validateSlotUC = new ValidateDeliverySlotUseCase(deliveryService);
        restaurantId = UUID.randomUUID();
    }

    @Test
    void testGetAvailableDeliverySlots() {
        LocalDate today = LocalDate.now();
        TimeSlot slot = new TimeSlot(restaurantId,
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(30),
                3);

        when(deliveryService.getAvailableSlots(today)).thenReturn(List.of(slot));

        var result = getAvailableUC.execute(today);

        verify(deliveryService).getAvailableSlots(today);
        assertEquals(1, result.size());
        assertEquals(slot.getId(), result.getFirst().id());
        assertTrue(result.getFirst().available());
    }

    @Test
    void testSelectDeliverySlotReturnsTrueWhenAvailable() {
        LocalDate today = LocalDate.now();
        UUID slotId = UUID.randomUUID();
        TimeSlot slot = new TimeSlot(slotId, restaurantId,
                LocalDateTime.now(), LocalDateTime.now().plusMinutes(30), 2);
        SelectDeliverySlotUseCase.Input input = new SelectDeliverySlotUseCase.Input(slotId, today);

        when(deliveryService.getAvailableSlots(today)).thenReturn(List.of(slot));

        boolean ok = selectSlotUC.execute(input);

        assertTrue(ok, "Le créneau devrait être sélectionnable");
        verify(deliveryService).getAvailableSlots(today);
    }

    @Test
    void testSelectDeliverySlotReturnsFalseWhenNotAvailable() {
        LocalDate today = LocalDate.now();
        UUID slotId = UUID.randomUUID();
        SelectDeliverySlotUseCase.Input input = new SelectDeliverySlotUseCase.Input(slotId, today);

        when(deliveryService.getAvailableSlots(today)).thenReturn(List.of());

        boolean ok = selectSlotUC.execute(input);
        assertFalse(ok, "Le créneau devrait être indisponible");
    }

    @Test
    void testValidateDeliverySlotReservesWhenStillAvailable() {
        LocalDate today = LocalDate.now();
        UUID slotId = UUID.randomUUID();
        TimeSlot slot = new TimeSlot(slotId, restaurantId,
                LocalDateTime.now(), LocalDateTime.now().plusMinutes(30), 2);
        ValidateDeliverySlotUseCase.Input input = new ValidateDeliverySlotUseCase.Input(slot.getId(), today);

        when(deliveryService.getAvailableSlots(today)).thenReturn(List.of(slot));

        validateSlotUC.execute(input);

        verify(deliveryService).getAvailableSlots(today);
        verify(deliveryService).reserveSlot(slotId);
    }

    @Test
    void testValidateDeliverySlotThrowsIfNotAvailable() {
        LocalDate today = LocalDate.now();
        UUID slotId = UUID.randomUUID();

        when(deliveryService.getAvailableSlots(today)).thenReturn(List.of());
        ValidateDeliverySlotUseCase.Input input = new ValidateDeliverySlotUseCase.Input(slotId, today);

        assertThrows(SlotNotFoundException.class, () -> validateSlotUC.execute(input));
        verify(deliveryService).getAvailableSlots(today);
        verify(deliveryService, never()).reserveSlot(any());
    }
}