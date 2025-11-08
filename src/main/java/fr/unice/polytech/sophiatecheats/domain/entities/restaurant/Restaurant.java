package fr.unice.polytech.sophiatecheats.domain.entities.restaurant;

import fr.unice.polytech.sophiatecheats.domain.entities.Entity;
import fr.unice.polytech.sophiatecheats.domain.entities.delivery.DeliverySchedule;
import fr.unice.polytech.sophiatecheats.domain.enums.DishCategory;
import fr.unice.polytech.sophiatecheats.domain.enums.RestaurantType;
import fr.unice.polytech.sophiatecheats.domain.exceptions.RestaurantValidationException;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

/**
 * Entité représentant un restaurant dans le système SophiaTech Eats.
 */
@Getter
@Setter
public class Restaurant implements Entity<UUID> {

    private final UUID id;
    private final String name;
    private final String address;
    private Schedule schedule;
    private boolean isOpen;
    private final List<Dish> menu;
    private final DeliverySchedule deliverySchedule;
    private RestaurantType restaurantType;
    private DishCategory cuisineType;

    /**
     * Constructeur privé utilisé par le Builder.
     */
    private Restaurant(Builder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.address = builder.address;
        this.schedule = builder.schedule != null ? builder.schedule : Schedule.defaultSchedule();
        this.isOpen = builder.isOpen;
        this.menu = builder.menu != null ? new ArrayList<>(builder.menu) : new ArrayList<>();
        this.deliverySchedule = builder.deliverySchedule != null ? builder.deliverySchedule : new DeliverySchedule(this.id);
        this.restaurantType = builder.restaurantType != null ? builder.restaurantType : RestaurantType.RESTAURANT;
        this.cuisineType = builder.cuisineType;
        validate();
    }

    /**
     * Retourne un nouveau Builder pour créer un Restaurant.
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Builder pour construire un Restaurant de manière fluide.
     */
    public static class Builder {
        private UUID id;
        private String name;
        private String address;
        private Schedule schedule;
        private boolean isOpen = true;
        private List<Dish> menu;
        private DeliverySchedule deliverySchedule;
        private RestaurantType restaurantType;
        private DishCategory cuisineType;

        private Builder() {
        }

        /**
         * Définit l'ID du restaurant (optionnel, un UUID aléatoire sera généré si non fourni).
         */
        public Builder id(UUID id) {
            this.id = id;
            return this;
        }

        /**
         * Définit le nom du restaurant (obligatoire).
         */
        public Builder name(String name) {
            this.name = name;
            return this;
        }

        /**
         * Définit l'adresse du restaurant (obligatoire).
         */
        public Builder address(String address) {
            this.address = address;
            return this;
        }

        /**
         * Définit le planning du restaurant (optionnel, un planning par défaut sera utilisé si non fourni).
         */
        public Builder schedule(Schedule schedule) {
            this.schedule = schedule;
            return this;
        }

        /**
         * Définit si le restaurant est ouvert (optionnel, true par défaut).
         */
        public Builder isOpen(boolean isOpen) {
            this.isOpen = isOpen;
            return this;
        }

        /**
         * Définit le menu du restaurant (optionnel, une liste vide sera créée si non fournie).
         */
        public Builder menu(List<Dish> menu) {
            this.menu = menu;
            return this;
        }

        /**
         * Définit le planning de livraison du restaurant (optionnel, un planning sera créé automatiquement si non fourni).
         */
        public Builder deliverySchedule(DeliverySchedule deliverySchedule) {
            this.deliverySchedule = deliverySchedule;
            return this;
        }

        /**
         * Définit le type de restaurant (optionnel, RESTAURANT par défaut).
         */
        public Builder restaurantType(RestaurantType restaurantType) {
            this.restaurantType = restaurantType;
            return this;
        }

        /**
         * Définit le type de cuisine du restaurant (optionnel).
         */
        public Builder cuisineType(DishCategory cuisineType) {
            this.cuisineType = cuisineType;
            return this;
        }

        /**
         * Construit le Restaurant avec les paramètres définis.
         * @return Un nouveau Restaurant
         * @throws RestaurantValidationException si les paramètres obligatoires sont manquants ou invalides
         */
        public Restaurant build() {
            // Générer un ID si non fourni
            if (this.id == null) {
                this.id = UUID.randomUUID();
            }
            return new Restaurant(this);
        }
    }

    /**
     * Constructeur pour créer un nouveau restaurant.
     */
    public Restaurant(String name, String address) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.address = address;
        this.menu = new ArrayList<>();
        this.isOpen = true;
        this.schedule = Schedule.defaultSchedule();
        this.deliverySchedule = new DeliverySchedule(this.id);
        this.restaurantType = RestaurantType.RESTAURANT;
        this.cuisineType = null;
        validate();
    }

    /**
     * Constructeur pour créer un nouveau restaurant avec type.
     */
    public Restaurant(String name, String address, RestaurantType restaurantType, DishCategory cuisineType) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.address = address;
        this.menu = new ArrayList<>();
        this.isOpen = true;
        this.schedule = Schedule.defaultSchedule();
        this.deliverySchedule = new DeliverySchedule(this.id);
        this.restaurantType = restaurantType != null ? restaurantType : RestaurantType.RESTAURANT;
        this.cuisineType = cuisineType;
        validate();
    }

    /**
     * Constructeur pour reconstruire un restaurant existant.
     */
    public Restaurant(UUID id, String name, String address,
                      Schedule schedule, boolean isOpen, List<Dish> menu,
                      DeliverySchedule deliverySchedule) {
        this(id, name, address, schedule, isOpen, menu, deliverySchedule, RestaurantType.RESTAURANT, null);
    }

    /**
     * Constructeur complet pour reconstruire un restaurant existant.
     */
    public Restaurant(UUID id, String name, String address,
                      Schedule schedule, boolean isOpen, List<Dish> menu,
                      DeliverySchedule deliverySchedule, RestaurantType restaurantType, DishCategory cuisineType) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.schedule = schedule != null ? schedule : Schedule.defaultSchedule();
        this.isOpen = isOpen;
        this.menu = menu != null ? new ArrayList<>(menu) : new ArrayList<>();
        this.deliverySchedule = deliverySchedule != null ? deliverySchedule : new DeliverySchedule(id);
        this.restaurantType = restaurantType != null ? restaurantType : RestaurantType.RESTAURANT;
        this.cuisineType = cuisineType;
        validate();
    }

    @Override
    public UUID getId() {
        return id;
    }

    @Override
    public void validate() {
        if (name == null || name.trim().isEmpty()) {
            throw new RestaurantValidationException("Le nom du restaurant ne peut pas être vide");
        }
        if (name.length() > 200) {
            throw new RestaurantValidationException("Le nom du restaurant ne peut pas dépasser 200 caractères");
        }
        if (address == null || address.trim().isEmpty()) {
            throw new RestaurantValidationException("L'adresse du restaurant ne peut pas être vide");
        }
        if (id == null) {
            throw new RestaurantValidationException("L'identifiant du restaurant ne peut pas être null");
        }
    }

    public boolean acceptsExternalCards() {
        return true;
    }

    public boolean acceptsStudentCredit() {
        return true;
    }

    public void addDish(Dish dish) {
        addDish(dish, false);
    }

    public void addDish(Dish dish, boolean allowDuplicateNames) {
        if (dish == null) {
            throw new IllegalArgumentException("Le plat ne peut pas être null");
        }
        // Check for duplicate dish name only if not explicitly allowing duplicates
        if (!allowDuplicateNames && menu.stream().anyMatch(existingDish -> existingDish.getName().equals(dish.getName()))) {
            throw new IllegalArgumentException("Un plat avec le nom '" + dish.getName() + "' existe déjà dans le menu");
        }
        // Assigner l'UUID du restaurant au plat afin que le plat connaisse son propriétaire.
        dish.setRestaurantId(this.id);
        menu.add(dish);
    }

    public void removeDish(UUID dishId) {
        if (dishId == null) {
            throw new IllegalArgumentException("L'identifiant du plat ne peut pas être null");
        }
        menu.removeIf(dish -> dish.getId().equals(dishId));
    }

    public List<Dish> getAvailableDishes() {
        return menu.stream()
                .filter(Dish::isAvailable)
                .toList();
    }

    public Optional<Dish> findDishById(UUID dishId) {
        if (dishId == null) {
            return Optional.empty();
        }
        return menu.stream()
                .filter(dish -> dish.getId().equals(dishId))
                .findFirst();
    }

    public Optional<Dish> findDishByName(String dishName) {
        if (dishName == null || dishName.trim().isEmpty()) {
            return Optional.empty();
        }
        return menu.stream()
                .filter(dish -> dish.getName().equals(dishName))
                .findFirst();
    }

    // Modifies existing dish attributes while preserving availability state
    private void modifyDish(UUID dishId, String name, String description, BigDecimal price, DishCategory category) {
        Optional<Dish> d = findDishById(dishId);
        if (d.isPresent()) {
            Dish existingDish = d.get();
            if (name == null) {
                name = existingDish.getName();
            }
            if (description == null) {
                description = existingDish.getDescription();
            }
            if (price == null) {
                price = existingDish.getPrice();
            }
            if (category == null) {
                category = existingDish.getCategory();
            }
            boolean currentAvailability = existingDish.isAvailable();
            removeDish(dishId);
            addDish(Dish.builder()
                    .id(dishId)
                    .name(name)
                    .description(description)
                    .price(price)
                    .category(category)
                    .available(currentAvailability)
                    .build());
        }
    }

    public void modifyDishName(UUID dishId, String newName) {
        modifyDish(dishId, newName, null, null, null);
    }

    public void modifyDishDescription(UUID dishId, String newDescription) {
        modifyDish(dishId, null, newDescription, null, null);
    }

    public void modifyDishPrice(UUID dishId, BigDecimal newPrice) {
        modifyDish(dishId, null, null, newPrice, null);
    }

    public void modifyDishCategory(UUID dishId, DishCategory newCategory) {
        modifyDish(dishId, null, null, null, newCategory);
    }

    public void reserveDeliverySlot(UUID slotId) {
        deliverySchedule.reserveSlot(slotId);
    }

    public void releaseDeliverySlot(UUID slotId) {
        deliverySchedule.releaseSlot(slotId);
    }

    public boolean isOpenAt(LocalTime time) {
        return isOpen && schedule.isOpenAt(time);
    }

    public boolean isOpenAt(LocalDateTime time) {
        return isOpen && schedule.isOpenAt(time);
    }

    public void setSchedule(LocalTime opening, LocalTime closing) {
        this.schedule = new Schedule(opening, closing);
    }

    public void setSchedule(Schedule newSchedule) {
        if (newSchedule == null) {
            throw new IllegalArgumentException("Le planning ne peut pas être null");
        }
        this.schedule = newSchedule;
    }

    public LocalTime getOpeningTime() {
        return schedule != null ? schedule.openingTime() : null;
    }

    public LocalTime getClosingTime() {
        return schedule != null ? schedule.closingTime() : null;
    }

    public void open() {
        this.isOpen = true;
    }

    public void close() {
        this.isOpen = false;
    }

    public List<Dish> getMenu() {
        return new ArrayList<>(menu);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Restaurant that = (Restaurant) obj;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("Restaurant{id=%s, name='%s', address='%s', isOpen=%s}",
            id, name, address, isOpen);
    }
}
