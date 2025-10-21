package fr.unice.polytech.sophiatecheats.domain.entities.restaurant;

import fr.unice.polytech.sophiatecheats.domain.entities.delivery.DeliverySchedule;
import fr.unice.polytech.sophiatecheats.domain.enums.DishCategory;
import fr.unice.polytech.sophiatecheats.domain.enums.RestaurantType;
import fr.unice.polytech.sophiatecheats.domain.exceptions.RestaurantValidationException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class RestaurantTest {


    @Test
    void shouldCreateRestaurantUsingBuilder() {
        Restaurant restaurant = Restaurant.builder()
                .name("Pizza Palace")
                .address("123 Main Street")
                .build();

        assertNotNull(restaurant.getId());
        assertEquals("Pizza Palace", restaurant.getName());
        assertEquals("123 Main Street", restaurant.getAddress());
        assertTrue(restaurant.isOpen());
        assertTrue(restaurant.getMenu().isEmpty());
        assertEquals(RestaurantType.RESTAURANT, restaurant.getRestaurantType());
    }

    @Test
    void shouldCreateRestaurantWithBuilderAndAllParameters() {
        UUID customId = UUID.randomUUID();
        Schedule customSchedule = new Schedule(LocalTime.of(10, 0), LocalTime.of(22, 0));
        List<Dish> menu = new ArrayList<>();
        menu.add(Dish.builder()
                .name("Pasta")
                .description("Italian pasta")
                .price(new BigDecimal("14.99"))
                .category(DishCategory.MAIN_COURSE)
                .build());

        Restaurant restaurant = Restaurant.builder()
                .id(customId)
                .name("Italian Restaurant")
                .address("456 Rome Street")
                .schedule(customSchedule)
                .isOpen(false)
                .menu(menu)
                .restaurantType(RestaurantType.FAST_FOOD)
                .cuisineType(DishCategory.MAIN_COURSE)
                .build();

        assertEquals(customId, restaurant.getId());
        assertEquals("Italian Restaurant", restaurant.getName());
        assertEquals("456 Rome Street", restaurant.getAddress());
        assertFalse(restaurant.isOpen());
        assertEquals(1, restaurant.getMenu().size());
        assertEquals(RestaurantType.FAST_FOOD, restaurant.getRestaurantType());
        assertEquals(DishCategory.MAIN_COURSE, restaurant.getCuisineType());
        assertEquals(LocalTime.of(10, 0), restaurant.getOpeningTime());
        assertEquals(LocalTime.of(22, 0), restaurant.getClosingTime());
    }

    @Test
    void shouldGenerateUuidWhenNotProvidedInBuilder() {
        Restaurant restaurant1 = Restaurant.builder()
                .name("Restaurant 1")
                .address("Address 1")
                .build();

        Restaurant restaurant2 = Restaurant.builder()
                .name("Restaurant 2")
                .address("Address 2")
                .build();

        assertNotNull(restaurant1.getId());
        assertNotNull(restaurant2.getId());
        assertNotEquals(restaurant1.getId(), restaurant2.getId());
    }

    @Test
    void shouldCreateRestaurantWithBuilderAndDefaultValues() {
        Restaurant restaurant = Restaurant.builder()
                .name("Default Restaurant")
                .address("Default Address")
                .build();

        assertNotNull(restaurant.getSchedule());
        assertTrue(restaurant.isOpen());
        assertNotNull(restaurant.getDeliverySchedule());
        assertEquals(RestaurantType.RESTAURANT, restaurant.getRestaurantType());
        assertNull(restaurant.getCuisineType());
    }

    @Test
    void shouldThrowValidationErrorWhenBuildingWithInvalidName() {
        assertThrows(RestaurantValidationException.class, () ->
                Restaurant.builder()
                        .name("")
                        .address("Valid Address")
                        .build()
        );
    }

    @Test
    void shouldThrowValidationErrorWhenBuildingWithInvalidAddress() {
        assertThrows(RestaurantValidationException.class, () ->
                Restaurant.builder()
                        .name("Valid Name")
                        .address(null)
                        .build()
        );
    }

    @Test
    void shouldCreateRestaurantWithBuilderFluentInterface() {
        // Démonstration de l'interface fluide du Builder
        Restaurant restaurant = Restaurant.builder()
                .name("Burger House")
                .address("789 Burger Lane")
                .restaurantType(RestaurantType.FAST_FOOD)
                .cuisineType(DishCategory.MAIN_COURSE)
                .isOpen(true)
                .build();

        assertEquals("Burger House", restaurant.getName());
        assertEquals(RestaurantType.FAST_FOOD, restaurant.getRestaurantType());
        assertTrue(restaurant.isOpen());
    }

    @Test
    void shouldCreateRestaurantWithCustomDeliverySchedule() {
        UUID restaurantId = UUID.randomUUID();
        DeliverySchedule customSchedule = new DeliverySchedule(restaurantId);

        Restaurant restaurant = Restaurant.builder()
                .id(restaurantId)
                .name("Delivery Restaurant")
                .address("Delivery Street")
                .deliverySchedule(customSchedule)
                .build();

        assertEquals(customSchedule, restaurant.getDeliverySchedule());
    }

    @Test
    void shouldCreateValidRestaurant() {
        String name = "Test Restaurant";
        String address = "123 Test Street";

        Restaurant restaurant = new Restaurant(name, address);

        assertNotNull(restaurant.getId());
        assertEquals(name, restaurant.getName());
        assertEquals(address, restaurant.getAddress());
        assertTrue(restaurant.isOpen());
        assertTrue(restaurant.getMenu().isEmpty());
    }

    void shouldFailWhen(String name, String address) {
        assertThrows(RestaurantValidationException.class,
                () -> new Restaurant(name, address));
    }

    @Test
    void shouldThrowWhenNameIsNull() {
        String address = "123 Test Street";
        shouldFailWhen(null, address);
    }

    @Test
    void shouldThrowWhenNameIsEmpty() {
        String name = "   ";
        String address = "123 Test Street";
        shouldFailWhen(name, address);
    }

    @Test
    void shouldThrowWhenNameIsTooLong() {
        String name = "a".repeat(201);
        String address = "123 Test Street";
        shouldFailWhen(name, address);
    }

    @Test
    void shouldThrowWhenAddressIsNull() {
        String name = "Test Restaurant";
        String address = null;
        shouldFailWhen(name, address);
    }

    @Test
    void shouldThrowWhenAddressIsEmpty() {
        String name = "Test Restaurant";
        String address = "   ";
        shouldFailWhen(name, address);
    }

    @Test
    void shouldAddDishToMenu() {
        Restaurant restaurant = new Restaurant("Test", "Address");
        Dish dish = Dish.builder()
                .name("Pizza")
                .description("Delicious pizza")
                .price(new BigDecimal("12.99"))
                .category(DishCategory.MAIN_COURSE)
                .build();

        restaurant.addDish(dish);

        assertEquals(1, restaurant.getMenu().size());
        assertTrue(restaurant.getMenu().contains(dish));
    }

    @Test
    void shouldThrowWhenAddingNullDish() {
        Restaurant restaurant = new Restaurant("Test", "Address");

        assertThrows(IllegalArgumentException.class,
                () -> restaurant.addDish(null));
    }

    @Test
    void shouldThrowWhenAddingDishWithDuplicateName() {
        Restaurant restaurant = new Restaurant("Test", "Address");
        Dish dish1 = Dish.builder()
                .name("Pizza")
                .description("First pizza")
                .price(new BigDecimal("12.99"))
                .category(DishCategory.MAIN_COURSE)
                .build();
        Dish dish2 = Dish.builder()
                .name("Pizza")
                .description("Second pizza")
                .price(new BigDecimal("15.99"))
                .category(DishCategory.MAIN_COURSE)
                .build();

        restaurant.addDish(dish1);

        assertThrows(IllegalArgumentException.class,
                () -> restaurant.addDish(dish2));
    }

    @Test
    void shouldSetSchedule() {
        Restaurant restaurant = new Restaurant("Test", "Address");
        LocalTime opening = LocalTime.of(9, 0);
        LocalTime closing = LocalTime.of(22, 0);

        restaurant.setSchedule(opening, closing);

        assertEquals(opening, restaurant.getOpeningTime());
        assertEquals(closing, restaurant.getClosingTime());
    }

    @Test
    void shouldThrowWhenOpeningAfterClosing() {
        Restaurant restaurant = new Restaurant("Test", "Address");
        LocalTime opening = LocalTime.of(22, 0);
        LocalTime closing = LocalTime.of(9, 0);

        assertThrows(RestaurantValidationException.class,
                () -> restaurant.setSchedule(opening, closing));
    }

    @Test
    void shouldOpenAndCloseRestaurant() {
        Restaurant restaurant = new Restaurant("Test", "Address");
        assertTrue(restaurant.isOpen());

        restaurant.close();
        assertFalse(restaurant.isOpen());

        restaurant.open();
        assertTrue(restaurant.isOpen());
    }

    @Test
    void shouldCheckOpenStatusAtSpecificTime() {
        Restaurant restaurant = new Restaurant("Test", "Address");
        restaurant.setSchedule(LocalTime.of(9, 0), LocalTime.of(18, 0));

        assertTrue(restaurant.isOpenAt(LocalTime.of(12, 0)));
        assertFalse(restaurant.isOpenAt(LocalTime.of(8, 0)));
        assertFalse(restaurant.isOpenAt(LocalTime.of(19, 0)));
    }

    @Test
    void shouldConstructRestaurantWithAllFields() {
        UUID id = UUID.randomUUID();
        String name = "Full Restaurant";
        String address = "123 Full Street";
        Schedule schedule = new Schedule(LocalTime.of(8, 0), LocalTime.of(22, 0));
        List<Dish> menu = List.of(
                Dish.builder()
                        .id(UUID.randomUUID())
                        .name("Test Dish")
                        .description("Description")
                        .price(new BigDecimal("10"))
                        .category(DishCategory.MAIN_COURSE)
                        .available(true)
                        .build()
        );
        boolean isOpen = false;
        DeliverySchedule deliverySchedule = new DeliverySchedule(id);

        Restaurant restaurant = new Restaurant(id, name, address, schedule, isOpen, menu, deliverySchedule);

        assertEquals(id, restaurant.getId());
        assertEquals(name, restaurant.getName());
        assertEquals(address, restaurant.getAddress());
        assertEquals(schedule.openingTime(), restaurant.getOpeningTime());
        assertEquals(schedule.closingTime(), restaurant.getClosingTime());
        assertEquals(1, restaurant.getMenu().size());
        assertFalse(restaurant.isOpen());
    }

    @Test
    void shouldCheckIfOpenAtSpecificDatetime() {
        Restaurant restaurant = new Restaurant("Test", "Address");
        restaurant.setSchedule(LocalTime.of(9, 0), LocalTime.of(18, 0));

        LocalDateTime morning = LocalDateTime.of(2025, 10, 10, 10, 30);
        LocalDateTime evening = LocalDateTime.of(2025, 10, 10, 20, 0);

        assertTrue(restaurant.isOpenAt(morning));
        assertFalse(restaurant.isOpenAt(evening));
        assertFalse(restaurant.isOpenAt((LocalDateTime) null));
    }

    @Test
    void shouldThrowWhenSettingInvalidSchedule() {
        Restaurant restaurant = new Restaurant("Test", "Address");

        assertThrows(RestaurantValidationException.class,
                () -> restaurant.setSchedule(LocalTime.of(22, 0), LocalTime.of(9, 0)));

        assertThrows(IllegalArgumentException.class,
                () -> restaurant.setSchedule(null));
    }

    @Test
    void shouldHaveReadableToString() {
        Restaurant restaurant = new Restaurant("Test Restaurant", "123 Test St");

        String result = restaurant.toString();

        assertTrue(result.contains("Test Restaurant"));
        assertTrue(result.contains("123 Test St"));
    }

    @Test
    void shouldCreateRestaurantWithNullScheduleParameters() {
        UUID id = UUID.randomUUID();
        Restaurant restaurant = new Restaurant(id, "Test", "Address", null, true, new ArrayList<>(), new DeliverySchedule(id));

        assertNotNull(restaurant.getSchedule());
    }

    @Test
    void shouldGenerateDeliverySlots() {
        Restaurant restaurant = new Restaurant("Test", "Address");

        restaurant.getDeliverySchedule().generateDailySlots(
                LocalDate.now().plusDays(1), LocalTime.of(8, 0), LocalTime.of(20, 30), 10
        );

        List<TimeSlot> slots = restaurant.getDeliverySchedule().getAvailableSlotsForDate(LocalDate.now().plusDays(1));
        assertFalse(slots.isEmpty());
    }

    @Test
    void should_not_equal_different_restaurants() {
        Restaurant restaurant1 = new Restaurant("Test1", "Address1");
        Restaurant restaurant2 = new Restaurant("Test2", "Address2");

        assertNotEquals(restaurant1, restaurant2);
        assertNotEquals(restaurant1, null);
        assertNotEquals(restaurant1, "not a restaurant");
    }

    @Test
    void shouldRemoveDishById() {
        Restaurant restaurant = new Restaurant("Test", "Address");
        Dish dish = Dish.builder()
                .name("Pizza")
                .description("Delicious pizza")
                .price(new BigDecimal("12.99"))
                .category(DishCategory.MAIN_COURSE)
                .build();
        restaurant.addDish(dish);

        restaurant.removeDish(dish.getId());

        assertTrue(restaurant.getMenu().isEmpty());
    }

    @Test
    void shouldFindDishById() {
        Restaurant restaurant = new Restaurant("Test", "Address");
        Dish dish = Dish.builder()
                .name("Pizza")
                .description("Delicious pizza")
                .price(new BigDecimal("12.99"))
                .category(DishCategory.MAIN_COURSE)
                .build();
        restaurant.addDish(dish);

        var found = restaurant.findDishById(dish.getId());

        assertTrue(found.isPresent());
        assertEquals(dish, found.get());
    }

    @Test
    void shouldFindDishByName() {
        Restaurant restaurant = new Restaurant("Test", "Address");
        Dish dish = Dish.builder()
                .name("Pizza")
                .description("Delicious pizza")
                .price(new BigDecimal("12.99"))
                .category(DishCategory.MAIN_COURSE)
                .build();
        restaurant.addDish(dish);

        var found = restaurant.findDishByName("Pizza");

        assertTrue(found.isPresent());
        assertEquals(dish, found.get());
    }

    @Test
    void shouldModifyDishProperties() {
        Restaurant restaurant = new Restaurant("Test", "Address");
        Dish dish = Dish.builder()
                .name("Pizza")
                .description("Delicious pizza")
                .price(new BigDecimal("12.99"))
                .category(DishCategory.MAIN_COURSE)
                .build();
        restaurant.addDish(dish);

        restaurant.modifyDishName(dish.getId(), "New Pizza");
        restaurant.modifyDishPrice(dish.getId(), new BigDecimal("15.99"));

        var found = restaurant.findDishById(dish.getId());
        assertTrue(found.isPresent());
        assertEquals("New Pizza", found.get().getName());
        assertEquals(new BigDecimal("15.99"), found.get().getPrice());
    }

    @Test
    void shouldReserveAndReleaseDeliverySlots() {
        Restaurant restaurant = new Restaurant("Test", "Address");
        restaurant.getDeliverySchedule().generateDailySlots(
                LocalDate.now().plusDays(1), LocalTime.of(8, 0), LocalTime.of(20, 0), 5
        );

        List<TimeSlot> slots = restaurant.getDeliverySchedule().getAvailableSlotsForDate(LocalDate.now().plusDays(1));
        assertFalse(slots.isEmpty());

        UUID slotId = slots.get(0).getId();
        restaurant.reserveDeliverySlot(slotId);

        restaurant.releaseDeliverySlot(slotId);
    }

    @Test
    void shouldAcceptPaymentMethods() {
        Restaurant restaurant = new Restaurant("Test", "Address");

        assertTrue(restaurant.acceptsExternalCards());
        assertTrue(restaurant.acceptsStudentCredit());
    }

    @Test
    void shouldGetAvailableDishes() {
        Restaurant restaurant = new Restaurant("Test", "Address");
        Dish availableDish = Dish.builder()
                .name("Available Pizza")
                .description("Available")
                .price(new BigDecimal("12.99"))
                .category(DishCategory.MAIN_COURSE)
                .build();
        Dish unavailableDish = Dish.builder()
                .name("Unavailable Pizza")
                .description("Unavailable")
                .price(new BigDecimal("15.99"))
                .category(DishCategory.MAIN_COURSE)
                .available(false)
                .build();

        restaurant.addDish(availableDish);
        restaurant.addDish(unavailableDish);

        List<Dish> availableDishes = restaurant.getAvailableDishes();

        assertEquals(1, availableDishes.size());
        assertTrue(availableDishes.contains(availableDish));
        assertFalse(availableDishes.contains(unavailableDish));
    }
}