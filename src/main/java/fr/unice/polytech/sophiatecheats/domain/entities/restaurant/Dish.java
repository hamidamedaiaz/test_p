package fr.unice.polytech.sophiatecheats.domain.entities.restaurant;

import fr.unice.polytech.sophiatecheats.domain.entities.Entity;
import fr.unice.polytech.sophiatecheats.domain.enums.DishCategory;
import fr.unice.polytech.sophiatecheats.domain.enums.DietType;
import fr.unice.polytech.sophiatecheats.domain.exceptions.DishValidationException;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/**
 * Entité représentant un plat dans le menu d'un restaurant.
 */
@Getter
public class Dish implements Entity<UUID>{

    private final UUID id;
    private final String name;
    private final String description;
    private BigDecimal price;
    private final DishCategory category;
    private boolean available;
    private final Set<DietType> dietTypes;



    // CONSTRUCTEUR PRIVÉ - Utiliser le Builder pour créer des instances
    private Dish(Builder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.description = builder.description;
        this.price = builder.price;
        this.category = builder.category;
        this.available = builder.available;
        this.dietTypes = new HashSet<>(builder.dietTypes);
        validate();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private UUID id = UUID.randomUUID();
        private String name;
        private String description;
        private BigDecimal price;
        private DishCategory category = DishCategory.MAIN_COURSE;
        private boolean available = true;
        private Set<DietType> dietTypes = new HashSet<>();

        public Builder id(UUID id) {
            this.id = id;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder price(BigDecimal price) {
            this.price = price;
            return this;
        }

        public Builder category(DishCategory category) {
            this.category = category;
            return this;
        }

        public Builder available(boolean available) {
            this.available = available;
            return this;
        }

        public Builder dietTypes(Set<DietType> dietTypes) {
            this.dietTypes = new HashSet<>(dietTypes);
            return this;
        }

        public Builder addDietType(DietType dietType) {
            this.dietTypes.add(dietType);
            return this;
        }

        public Dish build() {
            return new Dish(this);
        }
    }

    public void validate() {
        if (name == null || name.trim().isEmpty()) {
            throw new DishValidationException("Dish name is required");
        }
        if (name.length() > 200) {
            throw new DishValidationException("Le nom du plat ne peut pas dépasser 200 caractères");
        }
        if (description != null && description.length() > 500) {
            throw new DishValidationException("La description ne peut pas dépasser 500 caractères");
        }
        if (price == null || price.compareTo(BigDecimal.ZERO) < 0) {
            throw new DishValidationException("Price must be positive or zero");
        }
        if (category == null) {
            throw new DishValidationException("La catégorie du plat doit être définie");
        }
        if (id == null) {
            throw new DishValidationException("L'identifiant du plat ne peut pas être null");
        }
    }


    public void makeAvailable() {
        this.available = true;
    }

    public void makeUnavailable() {
        this.available = false;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    @Override
    public UUID getId() {
        return id;
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Dish dish = (Dish) obj;
        return Objects.equals(id, dish.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("Dish{id=%s, name='%s', price=%s, category=%s, available=%s}",
                           id, name, price, category, available);
    }

    public boolean hasDietType(DietType dietType) {
        return dietTypes.contains(dietType);
    }

    public Set<DietType> getDietTypes() {
        return new HashSet<>(dietTypes);
    }


}
