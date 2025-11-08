package fr.unice.polytech.sophiatecheats.application.usecases.user.delivery;

import fr.unice.polytech.sophiatecheats.application.usecases.UseCase;
import fr.unice.polytech.sophiatecheats.domain.services.DeliveryService;
import fr.unice.polytech.sophiatecheats.domain.exceptions.SlotNotFoundException;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Validation finale du créneau lors de la confirmation de commande.
 * Vérifie d'abord la disponibilité via DeliveryService.getAvailableSlots(...),
 * puis réserve de façon définitive via DeliveryService.reserveSlot(...)
 */
public class ValidateDeliverySlotUseCase implements UseCase<ValidateDeliverySlotUseCase.Input, Void> {

    private final DeliveryService deliveryService;

    public ValidateDeliverySlotUseCase(DeliveryService deliveryService) {
        this.deliveryService = deliveryService;
    }

    public record Input(UUID slotId, LocalDate date) {}

    @Override
    public Void execute(Input input) {
        boolean stillAvailable = deliveryService.getAvailableSlots(input.date).stream()
                .anyMatch(s -> s.getId().equals(input.slotId));

        if (!stillAvailable) {
            throw new SlotNotFoundException("Le créneau n'est plus disponible : " + input.slotId);
        }

        deliveryService.reserveSlot(input.slotId);
        return null;
    }
}
