package fr.unice.polytech.sophiatecheats.application.dto.restaurant.management;

import fr.unice.polytech.sophiatecheats.application.dto.DTO;
import fr.unice.polytech.sophiatecheats.domain.enums.DietType;
import fr.unice.polytech.sophiatecheats.domain.enums.DishCategory;

import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;

/**
 * DTO for dish data transfer between application layers.
 */
public record DishDto(
        UUID id,
        String name,
        String description,
        BigDecimal price,
        DishCategory category,
        boolean available,
        Set<DietType> dietTypes
) implements DTO {

    @Override
    public boolean isValid() {
        return id != null 
            && name != null && !name.trim().isEmpty()
            && description != null && !description.trim().isEmpty()
            && price != null && price.compareTo(BigDecimal.ZERO) >= 0
            && category != null;
    }
}