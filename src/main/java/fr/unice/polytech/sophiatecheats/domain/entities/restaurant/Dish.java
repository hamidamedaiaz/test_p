package fr.unice.polytech.sophiatecheats.domain.entities.restaurant;

import fr.unice.polytech.sophiatecheats.domain.entities.Entity;
import fr.unice.polytech.sophiatecheats.domain.enums.DishCategory;
import fr.unice.polytech.sophiatecheats.domain.exceptions.DishValidationException;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

/**
 * Entité représentant un plat dans le menu d'un restaurant.
 */
@Getter
@Setter
public class Dish implements Entity<UUID>{

    private final UUID id;
    private final String name;
    private final String description;
    private BigDecimal price;
    private final DishCategory category;
    private boolean available;

    /**
     * Constructeur complet avec tous les paramètres
     */
    public Dish(UUID id, String name, String description, BigDecimal price,
                DishCategory category, boolean available) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.category = category;
        this.available = available;
        validate();
    }

    /**
     * Constructeur principal pour créer un nouveau plat
     */
    public Dish(String name, String description, BigDecimal price, DishCategory category) {
        this(UUID.randomUUID(), name, description, price, category, true);
    }

    /**
     * Constructeur pour les tests avec disponibilité par défaut et catégorie MAIN_COURSE
     */
    public Dish(String name, String description, BigDecimal price) {
        this(name, description, price, DishCategory.MAIN_COURSE);
    }

    /**
     * Constructeur avec disponibilité personnalisée
     */
    public Dish(String name, String description, BigDecimal price, DishCategory category, boolean available) {
        this(UUID.randomUUID(), name, description, price, category, available);
    }


    public void validate() {
        if (name == null || name.trim().isEmpty()) {
            throw new DishValidationException("Le nom du plat ne peut pas être vide");
        }
        if (name.length() > 20) {
            throw new DishValidationException("Le nom du plat ne peut pas dépasser 20  caractères (: je dois verifeir ");
        }
        if (description != null && description.length() > 500) {
            throw new DishValidationException("La description ne peut pas dépasser 500 caractères");
        }
        if (price == null || price.compareTo(BigDecimal.ZERO) < 0) {
            throw new DishValidationException("Le prix du plat doit être positif ou nul");
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


}
