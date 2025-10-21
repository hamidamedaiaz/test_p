package fr.unice.polytech.sophiatecheats.application.dto.delivery;

import fr.unice.polytech.sophiatecheats.application.dto.DTO;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO for delivery slot data transfer between application layers.
 */
public record DeliverySlotDTO(UUID id,
                              UUID restaurantId,
                              LocalDateTime startTime,
                              LocalDateTime endTime,
                              int maxCapacity,
                              int reservedCount,
                              boolean available)
        implements DTO {
    @Override
    public boolean isValid() {
        return id != null && restaurantId != null
                && startTime != null && endTime != null
                && !endTime.isBefore(startTime)
                && maxCapacity >= 0 && reservedCount >= 0
                && reservedCount <= maxCapacity
                && available;
    }
}