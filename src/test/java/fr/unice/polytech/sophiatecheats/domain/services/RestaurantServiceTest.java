package fr.unice.polytech.sophiatecheats.domain.services;

import fr.unice.polytech.sophiatecheats.domain.entities.restaurant.TimeSlot;
import fr.unice.polytech.sophiatecheats.domain.entities.restaurant.Dish;
import fr.unice.polytech.sophiatecheats.domain.entities.restaurant.Restaurant;
import fr.unice.polytech.sophiatecheats.domain.enums.DishCategory;
import fr.unice.polytech.sophiatecheats.domain.exceptions.DuplicateRestaurantException;
import fr.unice.polytech.sophiatecheats.domain.exceptions.RestaurantNotFoundException;
import fr.unice.polytech.sophiatecheats.infrastructure.repositories.memory.InMemoryTimeSlotRepository;
import fr.unice.polytech.sophiatecheats.infrastructure.repositories.memory.InMemoryRestaurantRepository;
import fr.unice.polytech.sophiatecheats.domain.repositories.RestaurantRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RestaurantServiceTest {

    private RestaurantRepository repository;
    private RestaurantService service;

    @BeforeEach
    void setUp() {
        repository = Mockito.mock(RestaurantRepository.class);
        service = new RestaurantService(repository);
    }

    @Test
    void testCreateRestaurantCallsRepositorySave() {
        String name = "CROUS";
        String address = "Valbonne";

        service.createRestaurant(name, address);

        ArgumentCaptor<Restaurant> captor = ArgumentCaptor.forClass(Restaurant.class);
        verify(repository, times(1)).save(captor.capture());

        Restaurant saved = captor.getValue();
        assertEquals(name, saved.getName());
        assertEquals(address, saved.getAddress());
        assertNotNull(saved.getId());
    }

    @Test
    void testCreateRestaurantWithEmptyNameThrowsException() {
        assertThrows(RuntimeException.class, () -> service.createRestaurant("", "Nice"));
        verify(repository, never()).save(any());
    }

    @Test
    void testListRestaurantsDelegatesToRepository() {
        List<Restaurant> fakeList = List.of(
                new Restaurant("CROUS", "Valbonne"),
                new Restaurant("Mensa", "Nice")
        );
        when(repository.findAll()).thenReturn(fakeList);
        List<Restaurant> result = service.listRestaurants();
        verify(repository, times(1)).findAll();
        assertEquals(2, result.size());
        assertEquals("CROUS", result.getFirst().getName());
    }

    @Test
    void testGetRestaurantByIdReturnsRestaurantIfFound() {
        Restaurant r = new Restaurant("CROUS", "Valbonne");
        when(repository.findById(r.getId())).thenReturn(Optional.of(r));

        Restaurant found = service.getRestaurantById(r.getId());

        assertEquals("CROUS", found.getName());
        verify(repository, times(1)).findById(r.getId());
    }

    @Test
    void testGetRestaurantByIdThrowsIfNotFound() {
        when(repository.findById(any())).thenReturn(Optional.empty());
        assertThrows(RestaurantNotFoundException.class, () -> service.getRestaurantById(UUID.randomUUID()));
        verify(repository, times(1)).findById(any());
    }

    @Test
    void testCreateAndFindRestaurant() {
        InMemoryRestaurantRepository repo = new InMemoryRestaurantRepository(false);
        service = new RestaurantService(repo);

        service.createRestaurant("CROUS", "Valbonne");

        Restaurant saved = repo.findAll().getFirst();
        assertEquals("CROUS", saved.getName());
        assertTrue(repo.findById(saved.getId()).isPresent());
    }

    @Test
    void testCreateDuplicateRestaurantThrowsDuplicateRestaurantException() {
        InMemoryRestaurantRepository repo = new InMemoryRestaurantRepository(false);
        service = new RestaurantService(repo);

        service.createRestaurant("CROUS", "Valbonne");

        assertThrows(DuplicateRestaurantException.class, () ->
                service.createRestaurant("CROUS", "Valbonne"));
    }

    @Test
    void testUpdateRestaurantNameSuccessfully() {
        InMemoryRestaurantRepository repo = new InMemoryRestaurantRepository(false);
        service = new RestaurantService(repo);

        service.createRestaurant("CROUS", "Valbonne");
        Restaurant saved = repo.findAll().getFirst();

        service.updateRestaurantName(saved.getId(), "New CROUS");

        Restaurant updated = repo.findById(saved.getId()).orElseThrow();
        assertEquals("New CROUS", updated.getName());
        assertEquals("Valbonne", updated.getAddress());
    }

    @Test
    void testUpdateRestaurantAddressSuccessfully() {
        InMemoryRestaurantRepository repo = new InMemoryRestaurantRepository(false);
        service = new RestaurantService(repo);

        service.createRestaurant("CROUS", "Valbonne");
        Restaurant saved = repo.findAll().getFirst();

        service.updateRestaurantAddress(saved.getId(), "Nice");

        Restaurant updated = repo.findById(saved.getId()).orElseThrow();
        assertEquals("CROUS", updated.getName());
        assertEquals("Nice", updated.getAddress());
    }

    @Test
    void testUpdateRestaurantNameDoesNothingIfNullOrBlank() {
        InMemoryRestaurantRepository repo = new InMemoryRestaurantRepository(false);
        service = new RestaurantService(repo);

        service.createRestaurant("CROUS", "Valbonne");
        Restaurant saved = repo.findAll().getFirst();

        service.updateRestaurantName(saved.getId(), null);
        service.updateRestaurantName(saved.getId(), "  ");

        Restaurant updated = repo.findById(saved.getId()).orElseThrow();
        assertEquals("CROUS", updated.getName());
    }

    @Test
    void testUpdateRestaurantAddressDoesNothingIfNullOrBlank() {
        InMemoryRestaurantRepository repo = new InMemoryRestaurantRepository(false);
        service = new RestaurantService(repo);

        service.createRestaurant("CROUS", "Valbonne");
        Restaurant saved = repo.findAll().getFirst();

        service.updateRestaurantAddress(saved.getId(), "");
        Restaurant updated = repo.findById(saved.getId()).orElseThrow();
        assertEquals("Valbonne", updated.getAddress());
    }

    @Test
    void testUpdateOpeningHoursSuccessfully() {
        InMemoryRestaurantRepository repo = new InMemoryRestaurantRepository(false);
        service = new RestaurantService(repo);

        service.createRestaurant("CROUS", "Valbonne");
        Restaurant saved = repo.findAll().getFirst();

        service.updateRestaurantOpeningHours(saved.getId(), LocalTime.of(8, 0), LocalTime.of(18, 0));

        Restaurant updated = repo.findById(saved.getId()).orElseThrow();
        assertEquals(LocalTime.of(8, 0), updated.getOpeningTime());
        assertEquals(LocalTime.of(18, 0), updated.getClosingTime());
    }

    @Test
    void testUpdateOpeningHoursThrowsIfRestaurantNotFound() {
        assertThrows(RestaurantNotFoundException.class,
                () -> service.updateRestaurantOpeningHours(UUID.randomUUID(), LocalTime.of(8, 0), LocalTime.of(18, 0)));
    }

    @Test
    void testOpenRestaurantSuccessfully() {
        InMemoryRestaurantRepository repo = new InMemoryRestaurantRepository(false);
        service = new RestaurantService(repo);

        service.createRestaurant("CROUS", "Valbonne");
        Restaurant saved = repo.findAll().getFirst();
        saved.close();

        service.openRestaurant(saved.getId());

        Restaurant updated = repo.findById(saved.getId()).orElseThrow();
        assertTrue(updated.isOpen());
    }

    @Test
    void testCloseRestaurantSuccessfully() {
        InMemoryRestaurantRepository repo = new InMemoryRestaurantRepository(false);
        service = new RestaurantService(repo);

        service.createRestaurant("CROUS", "Valbonne");
        Restaurant saved = repo.findAll().getFirst();

        service.closeRestaurant(saved.getId());

        Restaurant updated = repo.findById(saved.getId()).orElseThrow();
        assertFalse(updated.isOpen());
    }

    @Test
    void testOpenOrCloseThrowsIfNotFound() {
        assertThrows(RestaurantNotFoundException.class, () -> service.openRestaurant(UUID.randomUUID()));
        assertThrows(RestaurantNotFoundException.class, () -> service.closeRestaurant(UUID.randomUUID()));
    }

    @Test
    void testDeleteRestaurantSuccessfully() {
        InMemoryRestaurantRepository repo = new InMemoryRestaurantRepository(false);
        service = new RestaurantService(repo);

        service.createRestaurant("CROUS", "Valbonne");
        Restaurant saved = repo.findAll().getFirst();

        service.deleteRestaurant(saved.getId());
        assertTrue(repo.findById(saved.getId()).isEmpty());
    }

    @Test
    void testDeleteRestaurantThrowsIfNotFound() {
        InMemoryRestaurantRepository repo = new InMemoryRestaurantRepository(false);
        service = new RestaurantService(repo);

        assertThrows(RestaurantNotFoundException.class, () -> service.deleteRestaurant(UUID.randomUUID()));
    }

    @Test
    void testAddDishToRestaurant() {
        InMemoryRestaurantRepository repo = new InMemoryRestaurantRepository(false);
        service = new RestaurantService(repo);

        service.createRestaurant("CROUS", "Valbonne");
        Restaurant r = repo.findAll().getFirst();

        service.addDishToRestaurant(r.getId(), "Pizza", "Delicious", BigDecimal.valueOf(8.5), DishCategory.MAIN_COURSE);

        List<Dish> dishes = r.getMenu();
        assertEquals(1, dishes.size());
        assertEquals("Pizza", dishes.getFirst().getName());
    }

    @Test
    void testRemoveDishFromRestaurant() {
        InMemoryRestaurantRepository repo = new InMemoryRestaurantRepository(false);
        service = new RestaurantService(repo);

        service.createRestaurant("CROUS", "Valbonne");
        Restaurant r = repo.findAll().getFirst();
        service.addDishToRestaurant(r.getId(), "Pizza", "Delicious", BigDecimal.valueOf(8.5), DishCategory.MAIN_COURSE);
        UUID dishId = r.getMenu().get(0).getId();

        service.removeDishFromRestaurant(r.getId(), dishId);

        assertTrue(r.getMenu().isEmpty());
    }

    @Test
    void testModifyDishProperties() {
        InMemoryRestaurantRepository repo = new InMemoryRestaurantRepository(false);
        service = new RestaurantService(repo);

        service.createRestaurant("CROUS", "Valbonne");
        Restaurant r = repo.findAll().getFirst();
        service.addDishToRestaurant(r.getId(), "Pizza", "Delicious", BigDecimal.valueOf(8.5), DishCategory.MAIN_COURSE);
        UUID dishId = r.getMenu().getFirst().getId();

        service.updateDishName(r.getId(), dishId, "Pasta");
        service.updateDishDescription(r.getId(), dishId, "Yummy");
        service.updateDishPrice(r.getId(), dishId, BigDecimal.valueOf(9.5));
        service.updateDishCategory(r.getId(), dishId, DishCategory.STARTER);

        Dish modified = r.getMenu().getFirst();
        assertEquals("Pasta", modified.getName());
        assertEquals("Yummy", modified.getDescription());
        assertEquals(BigDecimal.valueOf(9.5), modified.getPrice());
        assertEquals(DishCategory.STARTER, modified.getCategory());
    }

    @Test
    void testGetAvailableDishes() {
        InMemoryRestaurantRepository repo = new InMemoryRestaurantRepository(false);
        service = new RestaurantService(repo);

        service.createRestaurant("CROUS", "Valbonne");
        Restaurant r = repo.findAll().getFirst();
        service.addDishToRestaurant(r.getId(), "Pizza", "Delicious", BigDecimal.valueOf(8.5), DishCategory.MAIN_COURSE);

        List<Dish> available = service.getAvailableDishes(r.getId());
        assertEquals(1, available.size());
        assertEquals("Pizza", available.getFirst().getName());
    }

    @Test
    void testGenerateAndReserveDeliverySlots() {
        InMemoryRestaurantRepository repo = new InMemoryRestaurantRepository(false);
        service = new RestaurantService(repo);

        service.createRestaurant("CROUS", "Valbonne");
        Restaurant r = repo.findAll().getFirst();

        LocalDate date = LocalDate.now().plusDays(1);
        service.generateDeliverySlots(r.getId(), date, LocalTime.of(8, 0), LocalTime.of(10, 0), 2);

        List<TimeSlot> slots = service.getAvailableDeliverySlots(r.getId(), date);
        assertEquals(4, slots.size());

        UUID slotId = slots.getFirst().getId();
        service.reserveDeliverySlot(r.getId(), slotId);
        List<TimeSlot> afterReserve = service.getAvailableDeliverySlots(r.getId(), date);
        assertTrue(afterReserve.stream().anyMatch(s -> s.getReservedCount() == 1));

        service.releaseDeliverySlot(r.getId(), slotId);
        List<TimeSlot> afterRelease = service.getAvailableDeliverySlots(r.getId(), date);
        assertTrue(afterRelease.stream().allMatch(s -> s.getReservedCount() == 0));
    }

    @Test
    void testListRestaurantsByCategoryAndOpen() {
        InMemoryRestaurantRepository repo = new InMemoryRestaurantRepository(false);
        service = new RestaurantService(repo);

        service.createRestaurant("CROUS", "Valbonne");
        service.createRestaurant("Mensa", "Nice");

        List<Restaurant> all = repo.findAll();
        assertEquals(2, all.size());

        Restaurant r1 = all.stream().filter(r -> r.getName().equals("CROUS")).findFirst().orElseThrow();
        Restaurant r2 = all.stream().filter(r -> r.getName().equals("Mensa")).findFirst().orElseThrow();

        service.addDishToRestaurant(r1.getId(), "Pizza", "Delicious", BigDecimal.valueOf(8.5), DishCategory.MAIN_COURSE);
        service.addDishToRestaurant(r2.getId(), "Salad", "Healthy", BigDecimal.valueOf(5), DishCategory.STARTER);

        List<Restaurant> mains = service.listRestaurantsByCategory(DishCategory.MAIN_COURSE);
        assertEquals(1, mains.size());
        assertEquals("CROUS", mains.getFirst().getName());

        r2.close();
        List<Restaurant> openMains = service.listOpenRestaurantsByCategory(DishCategory.MAIN_COURSE);
        assertEquals(1, openMains.size());
        assertEquals("CROUS", openMains.getFirst().getName());
    }

    @Test
    void testGetOpenedRestaurantsTodayAt() {
        InMemoryRestaurantRepository repo = new InMemoryRestaurantRepository(false);
        service = new RestaurantService(repo);
        service.createRestaurant("CROUS", "Valbonne");
        service.createRestaurant("Mensa", "Nice");
        List<Restaurant> all = repo.findAll();
        assertEquals(2, all.size());

        Restaurant r1 = all.stream().filter(r -> r.getName().equals("CROUS")).findFirst().orElseThrow();
        Restaurant r2 = all.stream().filter(r -> r.getName().equals("Mensa")).findFirst().orElseThrow();

        service.updateRestaurantOpeningHours(r1.getId(), LocalTime.of(8, 0), LocalTime.of(20, 0));
        service.updateRestaurantOpeningHours(r2.getId(), LocalTime.of(10, 0), LocalTime.of(22, 0));
        r2.close();

        List<Restaurant> openAt9 = service.getOpenedRestaurantsTodayAt(LocalTime.of(9, 0));
        assertEquals(1, openAt9.size());
        assertEquals("CROUS", openAt9.getFirst().getName());
    }
}
