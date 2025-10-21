package fr.unice.polytech.sophiatecheats.application.usecases.user.delivery;

import fr.unice.polytech.sophiatecheats.domain.services.DeliveryService;
import fr.unice.polytech.sophiatecheats.domain.exceptions.SlotNotFoundException;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Validation finale du créneau lors de la confirmation de commande.
 * Vérifie d'abord la disponibilité via DeliveryService.getAvailableSlots(...),
 * puis réserve de façon définitive via DeliveryService.reserveSlot(...)
 */
public class ValidateDeliverySlotUseCase {

    private final DeliveryService deliveryService;

    public ValidateDeliverySlotUseCase(DeliveryService deliveryService) {
        this.deliveryService = deliveryService;
    }

    /**
     * Valide et réserve le créneau. Lance une exception si indisponible.
     */
    public void execute(UUID slotId, LocalDate date) {
        boolean stillAvailable = deliveryService.getAvailableSlots(date).stream()
                .anyMatch(s -> s.getId().equals(slotId));

        if (!stillAvailable) {
            throw new SlotNotFoundException("Le créneau n'est plus disponible : " + slotId);
        }

        deliveryService.reserveSlot(slotId);
    }
}
