package fr.unice.polytech.sophiatecheats.domain.services;

import fr.unice.polytech.sophiatecheats.domain.entities.restaurant.TimeSlot;
import fr.unice.polytech.sophiatecheats.domain.entities.restaurant.Dish;
import fr.unice.polytech.sophiatecheats.domain.entities.restaurant.Restaurant;
import fr.unice.polytech.sophiatecheats.domain.enums.DishCategory;
import fr.unice.polytech.sophiatecheats.domain.exceptions.RestaurantNotFoundException;
import fr.unice.polytech.sophiatecheats.infrastructure.repositories.memory.InMemoryRestaurantRepository;
import fr.unice.polytech.sophiatecheats.domain.repositories.RestaurantRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Service métier principal pour la gestion des restaurants.
 * Cette classe encapsule la logique métier associée aux opérations CRUD sur les entit��s Restaurant.
 * Responsabilités :
 *     Créer un nouveau restaurant après validation des données métier
 *     Mettre à jour les informations d’un restaurant existant
 *     Supprimer un restaurant de la base
 *     Récupérer un ou plusieurs restaurants enregistrés
 * Les exceptions métiers telles que {@link RestaurantNotFoundException}
 * sont levées en cas d’erreur ou de violation de règle fonctionnelle.
 */
public class RestaurantService {
    private final RestaurantRepository repository;

    public RestaurantService(RestaurantRepository repository) {
        this.repository = repository;
    }

    public void createRestaurant(String nom, String adresse) {
        Restaurant r = new Restaurant(nom, adresse);
        repository.save(r);
    }

    public List<Restaurant> listRestaurants() {
        return repository.findAll();
    }

    public Restaurant getRestaurantById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new RestaurantNotFoundException(id.toString()));
    }

    public void updateRestaurantName(UUID id, String newName) {
        Restaurant r = getRestaurantById(id);
        if (newName == null || newName.isBlank()) return;
        Restaurant updated = new Restaurant(
                r.getId(), newName, r.getAddress(),
                r.getSchedule(), r.isOpen(), r.getMenu(), r.getDeliverySchedule()
        );
        repository.save(updated);
    }

    public void updateRestaurantAddress(UUID id, String newAddress) {
        Restaurant r = getRestaurantById(id);
        if (newAddress == null || newAddress.isBlank()) return;
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
                .orElseThrow(() -> new RestaurantNotFoundException(id.toString()));

        if (repository instanceof InMemoryRestaurantRepository repoMem) {
            repoMem.delete(r);
        }
    }

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

    public List<TimeSlot> getAvailableDeliverySlots(UUID restaurantId, LocalDate date) {
        Restaurant r = getRestaurantById(restaurantId);
        return r.getDeliverySchedule().getAvailableSlotsForDate(date);
    }

    public List<Restaurant> listRestaurantsByCategory(DishCategory category) {
        if (repository instanceof InMemoryRestaurantRepository repo) {
            return repo.findByDishCategory(category);
        }
        return new ArrayList<>();
    }

    public List<Restaurant> listOpenRestaurantsByCategory(DishCategory category) {
        if (repository instanceof InMemoryRestaurantRepository repo) {
            return repo.findOpenByDishCategory(category);
        }
        return new ArrayList<>();
    }

    public List<Restaurant> getOpenedRestaurantsTodayAt(LocalTime time) {
        return repository.findAll().stream()
                .filter(restaurant -> restaurant.isOpenAt(time))
                .toList();
    }

}