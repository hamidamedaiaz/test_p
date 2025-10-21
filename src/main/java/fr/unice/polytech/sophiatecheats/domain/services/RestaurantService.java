package fr.unice.polytech.sophiatecheats.domain.services;

import fr.unice.polytech.sophiatecheats.domain.entities.order.Order;
import fr.unice.polytech.sophiatecheats.domain.entities.restaurant.TimeSlot;
import fr.unice.polytech.sophiatecheats.domain.entities.restaurant.Dish;
import fr.unice.polytech.sophiatecheats.domain.entities.restaurant.Restaurant;
import fr.unice.polytech.sophiatecheats.domain.enums.DishCategory;
import fr.unice.polytech.sophiatecheats.domain.exceptions.DuplicateRestaurantException;
import fr.unice.polytech.sophiatecheats.domain.exceptions.RestaurantNotFoundException;
import fr.unice.polytech.sophiatecheats.domain.exceptions.RestaurantValidationException;
import fr.unice.polytech.sophiatecheats.domain.repositories.RestaurantRepository;
import fr.unice.polytech.sophiatecheats.infrastructure.repositories.memory.InMemoryRestaurantRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Service métier principal pour la gestion des restaurants.
 * Cette classe encapsule la logique métier associée aux opérations CRUD sur les entités Restaurant.
 * Responsabilités :
 *     Créer un nouveau restaurant après validation des données métier
 *     Mettre à jour les informations d’un restaurant existant
 *     Supprimer un restaurant de la base
 *     Gérer le menu et les créneaux de livraison cotés métier pour un restaurant
 */
public class RestaurantService {
    private final RestaurantRepository repository;

    public RestaurantService(RestaurantRepository repository) {
        this.repository = repository;
    }

    // GESTION DES RESTAURANTS - CRUD
    public Restaurant createRestaurant(String name, String address) {
        if (name == null || name.isBlank()) {
            throw new RestaurantValidationException("The restaurant name cannot be empty");
        }
        if (address == null || address.isBlank()) {
            throw new RestaurantValidationException("The restaurant address cannot be empty");
        }
        boolean exists = repository.findAll().stream()
                .anyMatch(r -> r.getName().equalsIgnoreCase(name) && r.getAddress().equalsIgnoreCase(address));
        if (exists) throw new DuplicateRestaurantException("Restaurant déjà existant : " + name, address);

        Restaurant r = new Restaurant(name, address);
        repository.save(r);
        return r;
    }

    public List<Restaurant> listRestaurants() {
        return repository.findAll();
    }

    public Restaurant getRestaurantById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new RestaurantNotFoundException("Restaurant introuvable : " + id));
    }

    public void updateRestaurantName(UUID id, String newName) {
        Restaurant r = getRestaurantById(id);
        if (newName == null || newName.isBlank()) {
            throw new RestaurantValidationException("The restaurant name cannot be empty");
        }
        Restaurant updated = new Restaurant(
                r.getId(), newName, r.getAddress(),
                r.getSchedule(), r.isOpen(), r.getMenu(), r.getDeliverySchedule()
        );
        repository.save(updated);
    }

    public void updateRestaurantAddress(UUID id, String newAddress) {
        Restaurant r = getRestaurantById(id);
        if (newAddress == null || newAddress.isBlank()) {
            throw new RestaurantValidationException("The restaurant address cannot be empty");
        }
        Restaurant updated = new Restaurant(
                r.getId(), r.getName(), newAddress,
                r.getSchedule(), r.isOpen(), r.getMenu(), r.getDeliverySchedule()
        );
        repository.save(updated);
    }

    public void updateRestaurantOpeningHours(UUID id, LocalTime opening, LocalTime closing) {
        Restaurant r = getRestaurantById(id);
        r.setSchedule(opening, closing);
        repository.save(r);
    }

    public void openRestaurant(UUID id) {
        Restaurant r = getRestaurantById(id);
        r.open();
        repository.save(r);
    }

    public void closeRestaurant(UUID id) {
        Restaurant r = getRestaurantById(id);
        r.close();
        repository.save(r);
    }

    public void deleteRestaurant(UUID id) {
        Restaurant r = repository.findById(id)
                .orElseThrow(() -> new RestaurantNotFoundException("Restaurant introuvable : " + id));

        if (repository instanceof InMemoryRestaurantRepository repoMem) {
            repoMem.delete(r);
        }
    }

    //GESTION DU MENU
    public void addDishToRestaurant(UUID restaurantId, String name, String description, BigDecimal price, DishCategory category) {
        Restaurant r = getRestaurantById(restaurantId);
        r.addDish(Dish.builder()
                .name(name)
                .description(description)
                .price(price)
                .category(category)
                .available(true)
                .build());
        repository.save(r);
    }

    public void removeDishFromRestaurant(UUID restaurantId, UUID dishId) {
        Restaurant r = getRestaurantById(restaurantId);
        r.removeDish(dishId);
        repository.save(r);
    }

    public void updateDishName(UUID restaurantId, UUID dishId, String newName) {
        Restaurant r = getRestaurantById(restaurantId);
        r.modifyDishName(dishId, newName);
        repository.save(r);
    }

    public void updateDishDescription(UUID restaurantId, UUID dishId, String newDescription) {
        Restaurant r = getRestaurantById(restaurantId);
        r.modifyDishDescription(dishId, newDescription);
        repository.save(r);
    }

    public void updateDishPrice(UUID restaurantId, UUID dishId, BigDecimal newPrice) {
        Restaurant r = getRestaurantById(restaurantId);
        r.modifyDishPrice(dishId, newPrice);
        repository.save(r);
    }

    public void updateDishCategory(UUID restaurantId, UUID dishId, DishCategory newCategory) {
        Restaurant r = getRestaurantById(restaurantId);
        r.modifyDishCategory(dishId, newCategory);
        repository.save(r);
    }

    public List<Dish> getRestaurantMenu(UUID restaurantId) {
        Restaurant r = getRestaurantById(restaurantId);
        return r.getMenu();
    }

    public List<Dish> getAvailableDishes(UUID restaurantId) {
        Restaurant r = getRestaurantById(restaurantId);
        return r.getAvailableDishes();
    }

    // FILTRAGE DES RESTAURANTS
    public List<Restaurant> listRestaurantsByCategory(DishCategory category) {
        return repository.findByDishCategory(category);
    }

    public List<Restaurant> listOpenRestaurants() {
        return repository.findByAvailability(true);
    }

    public List<Restaurant> listOpenRestaurantsByCategory(DishCategory category) {
        return repository.findOpenByDishCategory(category);
    }

    public void generateDeliverySlots(UUID restaurantId, LocalDate date, LocalTime start, LocalTime end, int maxCapacityPerSlot) {
        Restaurant r = getRestaurantById(restaurantId);
        r.getDeliverySchedule().generateDailySlots(date, start, end, maxCapacityPerSlot);
        repository.save(r);
    }

    public List<TimeSlot> getDeliverySlots(UUID restaurantId, LocalDate date) {
        Restaurant r = getRestaurantById(restaurantId);
        return r.getDeliverySchedule().getAvailableSlotsForDate(date);
    }

    public void reserveDeliverySlot(UUID restaurantId, UUID slotId) {
        Restaurant r = getRestaurantById(restaurantId);
        r.reserveDeliverySlot(slotId);
        repository.save(r);
    }

    public void releaseDeliverySlot(UUID restaurantId, UUID slotId) {
        Restaurant r = getRestaurantById(restaurantId);
        r.releaseDeliverySlot(slotId);
        repository.save(r);
    }

    public List<Restaurant> getOpenedRestaurantsTodayAt(LocalTime time) {
        return repository.findAll().stream()
                .filter(r -> r.isOpenAt(time))
                .toList();
    }

    public List<TimeSlot> getAvailableDeliverySlots(UUID id, LocalDate date) {
        Restaurant r = getRestaurantById(id);
        return r.getDeliverySchedule().getSlotsForDate(date).stream()
                .filter(TimeSlot::isAvailable)
                .toList();
    }

    /**
     * Récupère les créneaux horaires de livraison disponibles pour une commande donnée.
     * @param order La commande pour laquelle on souhaite obtenir les créneaux disponibles.
     * @return Liste des créneaux horaires disponibles pour la livraison de la commande.
     */
    public List<TimeSlot> getAvailableSlotsForOrder(Order order) {
        if (order == null || order.getRestaurant() == null) {
            throw new IllegalArgumentException("Order or restaurant cannot be null");
        }

        Restaurant restaurant = getRestaurantById(order.getRestaurant().getId());
        LocalDate today = LocalDate.now();
        LocalDateTime now = LocalDateTime.now();

        List<TimeSlot> slots = restaurant.getDeliverySchedule().getSlotsForDate(today);

        return slots.stream()
                .filter(TimeSlot::isAvailable)
                .filter(slot -> slot.getStartTime().isAfter(now) || slot.getStartTime().isEqual(now))
                .toList();
    }

}