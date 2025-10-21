package fr.unice.polytech.sophiatecheats.application.usecases.user.delivery;

import fr.unice.polytech.sophiatecheats.application.dto.delivery.DeliverySlotDTO;
import fr.unice.polytech.sophiatecheats.domain.entities.restaurant.TimeSlot;
import fr.unice.polytech.sophiatecheats.domain.services.DeliveryService;

import java.time.LocalDate;
import java.util.List;
/**
 * Use case : récupérer les créneaux de livraison disponibles pour une date donnée.
 */
public class GetAvailableDeliverySlotsUseCase {

    private final DeliveryService deliveryService;

    public GetAvailableDeliverySlotsUseCase(DeliveryService deliveryService) {
        this.deliveryService = deliveryService;
    }

    /**
     * Récupère les créneaux pour une date donnée.
     */
    public List<DeliverySlotDTO> execute(LocalDate date) {
        return deliveryService.getAvailableSlots(date).stream()
                .map(this::toDto)
                .toList();
    }

    private DeliverySlotDTO toDto(TimeSlot s) {
        boolean available = s.isAvailable();
        return new DeliverySlotDTO(s.getId(), s.getRestaurantId(), s.getStartTime(), s.getEndTime(), s.getMaxCapacity(), s.getReservedCount(), available);
    }
}
