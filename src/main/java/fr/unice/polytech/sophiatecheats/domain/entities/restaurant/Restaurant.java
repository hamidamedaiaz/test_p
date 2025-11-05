package fr.unice.polytech.sophiatecheats.domain.entities.restaurant;

import fr.unice.polytech.sophiatecheats.domain.entities.Entity;
import fr.unice.polytech.sophiatecheats.domain.exceptions.RestaurantValidationException;
import lombok.Getter;
import lombok.Setter;

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
    private LocalTime openingTime;
    private LocalTime closingTime;
    private int maxCapacityPerSlot;
    private final Map<TimeSlot, CapacitySlot> capacitySlots;
    private final List<Dish> menu;
    private boolean isOpen;

    /**
     * Constructeur pour créer un nouveau restaurant.
     */
    public Restaurant(String name, String address) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.address = address;
        this.capacitySlots = new HashMap<>();
        this.menu = new ArrayList<>();
        this.isOpen = true;
        this.maxCapacityPerSlot = 10;
        validate();
    }

    /**
     * Constructeur pour reconstruire un restaurant existant.
     */
    public Restaurant(UUID id, String name, String address,
                     LocalTime openingTime, LocalTime closingTime, int maxCapacityPerSlot,
                     List<Dish> menu, boolean isOpen) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.openingTime = openingTime;
        this.closingTime = closingTime;
        this.maxCapacityPerSlot = maxCapacityPerSlot;
        this.capacitySlots = new HashMap<>();
        this.menu = menu != null ? new ArrayList<>(menu) : new ArrayList<>();
        this.isOpen = isOpen;
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

        if (openingTime != null && closingTime != null && openingTime.isAfter(closingTime)) {
            throw new RestaurantValidationException("L'heure d'ouverture ne peut pas être après l'heure de fermeture");
        }
        if (maxCapacityPerSlot < 0) {
            throw new RestaurantValidationException("La capacité maximale par créneau ne peut pas être négative");
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
        if (dish == null) {
            throw new IllegalArgumentException("Le plat ne peut pas être null");
        }
        if (!menu.contains(dish)) {
            menu.add(dish);
        }
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

    public boolean isSlotAvailable(TimeSlot slot) {
        if (slot == null) {
            return false;
        }
        if (!isOpenAt(slot.getStartTime())) {
            return false;
        }

        CapacitySlot capacitySlot = capacitySlots.get(slot);
        if (capacitySlot == null) {
            return true;
        }

        return capacitySlot.hasAvailableCapacity();
    }

    public void reserveSlot(TimeSlot slot) {
        if (slot == null) {
            throw new IllegalArgumentException("Le créneau ne peut pas être null");
        }
        CapacitySlot capacitySlot = capacitySlots.computeIfAbsent(
                slot,
                k -> new CapacitySlot(slot.getId(), maxCapacityPerSlot)
        );
        if (!capacitySlot.reserve()) {
            throw new IllegalStateException("Impossible de réserver le créneau : capacité maximale atteinte");
        }
    }

    public void releaseSlot(TimeSlot slot) {
        if (slot == null) {
            return;
        }
        CapacitySlot capacitySlot = capacitySlots.get(slot);
        if (capacitySlot != null) {
            capacitySlot.release();
        }
    }

    public List<TimeSlot> getAvailableSlots() {
        if (openingTime == null || closingTime == null) {
            return new ArrayList<>();
        }

        List<TimeSlot> availableSlots = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfDay = now.toLocalDate().atTime(openingTime);
        LocalDateTime endOfDay = now.toLocalDate().atTime(closingTime);

        LocalDateTime current = startOfDay;
        while (current.isBefore(endOfDay)) {
            TimeSlot slot = new TimeSlot(this.id, current.toLocalTime(), current, maxCapacityPerSlot);
            if (isSlotAvailable(slot)) {
                availableSlots.add(slot);
            }
            current = current.plusMinutes(30);
        }

        return availableSlots;
    }

    public boolean isOpenAt(LocalTime time) {
        if (time == null || openingTime == null || closingTime == null) {
            return false;
        }
        return isOpen &&
                !time.isBefore(openingTime) &&
                !time.isAfter(closingTime);
    }

    public boolean isOpenAt(LocalDateTime time) {
        if (time == null) {
            return false;
        }
        LocalTime timeOfDay = time.toLocalTime();
        return isOpenAt(timeOfDay);
    }




    public void setOpeningHours(LocalTime opening, LocalTime closing) {
        if (opening == null || closing == null) {
            throw new IllegalArgumentException("Les heures d'ouverture et de fermeture ne peuvent pas être null");
        }
        if (opening.isAfter(closing)) {
            throw new IllegalArgumentException("L'heure d'ouverture ne peut pas être après l'heure de fermeture");
        }
        this.openingTime = opening;
        this.closingTime = closing;
    }

    public void setMaxCapacityPerSlot(int capacity) {
        if (capacity < 0) {
            throw new IllegalArgumentException("La capacité maximale par créneau ne peut pas être négative");
        }
        this.maxCapacityPerSlot = capacity;
    }

    public void open() {
        this.isOpen = true;
    }

    public void close() {
        this.isOpen = false;
    }





    public String getName() { return name; }
    public String getAddress() { return address; }
    public LocalTime getOpeningTime() { return openingTime; }
    public LocalTime getClosingTime() { return closingTime; }
    public int getMaxCapacityPerSlot() { return maxCapacityPerSlot; }
    public List<Dish> getMenu() { return new ArrayList<>(menu); }
    public boolean isOpen() { return isOpen; }


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
        return String.format("Restaurant{id=%s, name='%s', address='%s', open=%s}",
                id, name, address, isOpen);
    }





}
