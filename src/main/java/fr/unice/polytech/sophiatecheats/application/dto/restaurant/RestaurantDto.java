package fr.unice.polytech.sophiatecheats.application.dto.restaurant;

import fr.unice.polytech.sophiatecheats.application.dto.DTO;
import fr.unice.polytech.sophiatecheats.application.dto.restaurant.management.DishDto;

import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

/**
 * DTO for restaurant data transfer between application layers.
 */
public record RestaurantDto(
        UUID id,
        String name,
        String address,
        LocalTime openingTime,
        LocalTime closingTime,
        boolean isOpen,
        List<DishDto> dishes
) implements DTO {

    @Override
    public boolean isValid() {
        return id != null 
            && name != null && !name.trim().isEmpty()
            && address != null && !address.trim().isEmpty()
            && dishes != null;
    }
}