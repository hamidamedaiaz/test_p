package fr.unice.polytech.sophiatecheats.application.usecases.user.delivery;

import fr.unice.polytech.sophiatecheats.domain.services.DeliveryService;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Sélection simple d'un créneau, pas de réservation définitive.
 */
public class SelectDeliverySlotUseCase {

    private final DeliveryService deliveryService;

    public SelectDeliverySlotUseCase(DeliveryService deliveryService) {
        this.deliveryService = deliveryService;
    }

    /**
     * @param slotId id du créneau
     * @param date date pour laquelle on veut vérifier la sélection (UI fournit la date)
     * @return true si le créneau est encore disponible
     */
    public boolean execute(UUID slotId, LocalDate date) {
        return deliveryService.getAvailableSlots(date).stream()
                .anyMatch(s -> s.getId().equals(slotId));
    }
}
