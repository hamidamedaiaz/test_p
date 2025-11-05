package fr.unice.polytech.sophiatecheats.domain.entities.restaurant;

import fr.unice.polytech.sophiatecheats.domain.entities.delivery.DeliverySchedule;
import fr.unice.polytech.sophiatecheats.domain.enums.DishCategory;
import fr.unice.polytech.sophiatecheats.domain.exceptions.CapacitySlotValidationException;
import fr.unice.polytech.sophiatecheats.domain.exceptions.RestaurantValidationException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class RestaurantTest {

    @Test
    void should_create_valid_restaurant() {
        // Given
        String name = "Test Restaurant";
        String address = "123 Test Street";

        // When
        Restaurant restaurant = new Restaurant(name, address);

        // Then
        assertNotNull(restaurant.getId());
        assertEquals(name, restaurant.getName());
        assertEquals(address, restaurant.getAddress());
        assertTrue(restaurant.isOpen());
        assertTrue(restaurant.getMenu().isEmpty());
    }

    @Test
    void should_fail_when_name_is_null() {
        // Given
        String name = null;
        String address = "123 Test Street";

        // When & Then
        assertThrows(RestaurantValidationException.class, 
            () -> new Restaurant(name, address));
    }

    @Test
    void should_fail_when_name_is_empty() {
        // Given
        String name = "   ";
        String address = "123 Test Street";

        // When & Then
        assertThrows(RestaurantValidationException.class, 
            () -> new Restaurant(name, address));
    }

    @Test
    void should_fail_when_address_is_null() {
        // Given
        String name = "Test Restaurant";
        String address = null;

        // When & Then
        assertThrows(RestaurantValidationException.class, 
            () -> new Restaurant(name, address));
    }

    @Test
    void should_add_dish_to_menu() {
        // Given
        Restaurant restaurant = new Restaurant("Test", "Address");
        Dish dish = new Dish(UUID.randomUUID(), "Pizza", "Delicious", new BigDecimal("10"), DishCategory.MAIN_COURSE, true);

        // When
        restaurant.addDish(dish);

        // Then
        assertEquals(1, restaurant.getMenu().size());
        assertTrue(restaurant.getMenu().contains(dish));
    }

    @Test
    void should_not_add_duplicate_dish() {
        // Given
        Restaurant restaurant = new Restaurant("Test", "Address");
        Dish dish = new Dish(UUID.randomUUID(), "Pizza", "Delicious", new BigDecimal("10"), DishCategory.MAIN_COURSE, true);

        // When
        restaurant.addDish(dish);
        restaurant.addDish(dish); // Add same dish again

        // Then
        assertEquals(1, restaurant.getMenu().size());
    }

    @Test
    void should_remove_dish_from_menu() {
        // Given
        Restaurant restaurant = new Restaurant("Test", "Address");
        Dish dish = new Dish(UUID.randomUUID(), "Pizza", "Delicious", new BigDecimal("10"), DishCategory.MAIN_COURSE, true);
        restaurant.addDish(dish);

        // When
        restaurant.removeDish(dish.getId());

        // Then
        assertTrue(restaurant.getMenu().isEmpty());
    }

    @Test
    void should_find_dish_by_id() {
        // Given
        Restaurant restaurant = new Restaurant("Test", "Address");
        Dish dish = new Dish(UUID.randomUUID(), "Pizza", "Delicious", new BigDecimal("10"), DishCategory.MAIN_COURSE, true);
        restaurant.addDish(dish);

        // When
        Optional<Dish> found = restaurant.findDishById(dish.getId());

        // Then
        assertTrue(found.isPresent());
        assertEquals(dish, found.get());
    }

    @Test
    void should_return_empty_when_dish_not_found() {
        // Given
        Restaurant restaurant = new Restaurant("Test", "Address");
        Dish dish = new Dish(UUID.randomUUID(), "Pizza", "Delicious", new BigDecimal("10"), DishCategory.MAIN_COURSE, true);

        // When
        Optional<Dish> found = restaurant.findDishById(dish.getId());

        // Then
        assertFalse(found.isPresent());
    }

    @Test
    void should_return_only_available_dishes() {
        // Given
        Restaurant restaurant = new Restaurant("Test", "Address");
        Dish availableDish = new Dish(UUID.randomUUID(), "Available", "Available dish", new BigDecimal("10"), DishCategory.MAIN_COURSE, true);
        Dish unavailableDish = new Dish(UUID.randomUUID(), "Unavailable", "Unavailable dish", new BigDecimal("15"), DishCategory.DESSERT, true);
        
        unavailableDish.makeUnavailable();
        restaurant.addDish(availableDish);
        restaurant.addDish(unavailableDish);

        // When
        List<Dish> availableDishes = restaurant.getAvailableDishes();

        // Then
        assertEquals(1, availableDishes.size());
        assertTrue(availableDishes.contains(availableDish));
        assertFalse(availableDishes.contains(unavailableDish));
    }

    /**
    @Test
    void should_modify_dish_name() {
        Restaurant restaurant = new Restaurant("Test", "Address");
        Dish dish = new Dish(UUID.randomUUID(), "Dish Name", "Dish Description", new BigDecimal("10"), DishCategory.MAIN_COURSE, true);
        restaurant.addDish(dish);

        assertEquals("Dish Name", restaurant.getMenu().getFirst().getName());

        //When
        restaurant.modifyDishName(restaurant.getMenu().getFirst().getId(),"New Dish Name");

        //Then
        assertEquals("New Dish Name", restaurant.getMenu().getFirst().getName());
    }

    @Test
    void should_modify_dish_description() {
        Restaurant restaurant = new Restaurant("Test", "Address");
        Dish dish = new Dish(UUID.randomUUID(), "Dish Name", "Dish Description", new BigDecimal("10"), DishCategory.MAIN_COURSE, true);
        restaurant.addDish(dish);

        assertEquals("Dish Description", restaurant.getMenu().getFirst().getDescription());

        //When
        restaurant.modifyDishDescription(restaurant.getMenu().getFirst().getId(),"New Dish Description");

        //Then
        assertEquals("New Dish Description", restaurant.getMenu().getFirst().getDescription());
    }

    @Test
    void should_modify_dish_price() {
        Restaurant restaurant = new Restaurant("Test", "Address");
        Dish dish = new Dish(UUID.randomUUID(), "Dish Name", "Dish Description", new BigDecimal("10"), DishCategory.MAIN_COURSE, true);
        restaurant.addDish(dish);

        assertEquals(new BigDecimal("10"), restaurant.getMenu().getFirst().getPrice());

        //When
        restaurant.modifyDishPrice(restaurant.getMenu().getFirst().getId(),new BigDecimal("20"));

        //Then
        assertEquals(new BigDecimal("20"), restaurant.getMenu().getFirst().getPrice());
    }

    @Test
    void should_modify_dish_category() {
        Restaurant restaurant = new Restaurant("Test", "Address");
        Dish dish = new Dish(UUID.randomUUID(), "Dish Name", "Dish Description", new BigDecimal("10"), DishCategory.MAIN_COURSE, true);
        restaurant.addDish(dish);

        assertEquals(DishCategory.MAIN_COURSE, restaurant.getMenu().getFirst().getCategory());

        //When
        restaurant.modifyDishCategory(restaurant.getMenu().getFirst().getId(),DishCategory.DESSERT);

        //Then
        assertEquals(DishCategory.DESSERT, restaurant.getMenu().getFirst().getCategory());
    }

    @Test
    void should_set_opening_hours() {
        // Given
        Restaurant restaurant = new Restaurant("Test", "Address");
        LocalTime opening = LocalTime.of(9, 0);
        LocalTime closing = LocalTime.of(22, 0);

        // When
        restaurant.setOpeningHours(opening, closing);

        // Then
        assertEquals(opening, restaurant.getOpeningTime());
        assertEquals(closing, restaurant.getClosingTime());
    }

    @Test
    void should_fail_when_opening_after_closing() {
        // Given
        Restaurant restaurant = new Restaurant("Test", "Address");
        LocalTime opening = LocalTime.of(22, 0);
        LocalTime closing = LocalTime.of(9, 0);

        // When & Then
        assertThrows(IllegalArgumentException.class, 
            () -> restaurant.setOpeningHours(opening, closing));
    }

    @Test
    void should_open_and_close_restaurant() {
        // Given
        Restaurant restaurant = new Restaurant("Test", "Address");
        assertTrue(restaurant.isOpen());

        // When
        restaurant.close();

        // Then
        assertFalse(restaurant.isOpen());

        // When
        restaurant.open();

        // Then
        assertTrue(restaurant.isOpen());
    }

    @Test
    void should_check_if_open_at_time() {
        // Given
        Restaurant restaurant = new Restaurant("Test", "Address");
        restaurant.setOpeningHours(LocalTime.of(9, 0), LocalTime.of(18, 0));

        // Then
        assertTrue(restaurant.isOpenAt(LocalTime.of(12, 0))); // Within hours
        assertFalse(restaurant.isOpenAt(LocalTime.of(8, 0))); // Before opening
        assertFalse(restaurant.isOpenAt(LocalTime.of(19, 0))); // After closing
    }

    @Test
    void should_accept_payments() {
        // Given
        Restaurant restaurant = new Restaurant("Test", "Address");

        // Then
        assertTrue(restaurant.acceptsExternalCards());
        assertTrue(restaurant.acceptsStudentCredit());
    }
     **/

    @Test
    void should_create_restaurant_with_full_constructor() {
        // Given
        UUID id = UUID.randomUUID();
        String name = "Full Restaurant";
        String address = "123 Full Street";
        LocalTime opening = LocalTime.of(8, 0);
        LocalTime closing = LocalTime.of(22, 0);
        List<Dish> menu = List.of(
            new Dish(UUID.randomUUID(), "Test Dish", "Description", new BigDecimal("10"), DishCategory.MAIN_COURSE, true)
        );
        boolean isOpen = false;
        DeliverySchedule deliverySchedule = new DeliverySchedule(id);

        // When
        Restaurant restaurant = new Restaurant(id, name, address, opening, closing, isOpen, menu, deliverySchedule);

        // Then
        assertEquals(id, restaurant.getId());
        assertEquals(name, restaurant.getName());
        assertEquals(address, restaurant.getAddress());
        assertEquals(opening, restaurant.getOpeningTime());
        assertEquals(closing, restaurant.getClosingTime());
        assertEquals(1, restaurant.getMenu().size());
        assertFalse(restaurant.isOpen());
    }

    @Test
    void should_check_if_open_at_datetime() {
        // Given
        Restaurant restaurant = new Restaurant("Test", "Address");
        restaurant.setOpeningHours(LocalTime.of(9, 0), LocalTime.of(18, 0));
        
        LocalDateTime morning = LocalDateTime.of(2025, 10, 10, 10, 30);
        LocalDateTime evening = LocalDateTime.of(2025, 10, 10, 20, 0);

        // Then
        assertTrue(restaurant.isOpenAt(morning)); // Within hours
        assertFalse(restaurant.isOpenAt(evening)); // After closing
        assertFalse(restaurant.isOpenAt((LocalDateTime) null)); // Null time
    }

    @Test
    void should_handle_null_opening_hours_in_isOpenAt() {
        // Given
        Restaurant restaurant = new Restaurant("Test", "Address");
        // openingTime and closingTime are null by default

        // When & Then
        assertFalse(restaurant.isOpenAt(LocalTime.of(12, 0)));
        assertFalse(restaurant.isOpenAt((LocalTime) null));
    }

    @Test
    void should_set_max_capacity_per_slot() {
        Restaurant restaurant = new Restaurant("Test", "Address");

        restaurant.generateDailyDeliverySlots(
                LocalDate.now(), LocalTime.of(8, 0), LocalTime.of(20, 30), 10
        );

        restaurant.getDeliverySlotsForDate(LocalDate.now())
                .forEach(slot -> slot.setMaxCapacity(25));

        assertEquals(25, restaurant.getMaxCapacityPerSlot());
    }

    @Test
    void should_fail_setting_invalid_opening_hours() {
        Restaurant restaurant = new Restaurant("Test", "Address");

        assertThrows(IllegalArgumentException.class,
            () -> restaurant.setOpeningHours(null, LocalTime.of(18, 0)));
        assertThrows(IllegalArgumentException.class, 
            () -> restaurant.setOpeningHours(LocalTime.of(9, 0), null));
        assertThrows(IllegalArgumentException.class, 
            () -> restaurant.setOpeningHours(LocalTime.of(18, 0), LocalTime.of(9, 0)));
    }

    @Test
    void should_have_meaningful_toString() {
        Restaurant restaurant = new Restaurant("Test Restaurant", "123 Test St");

        String result = restaurant.toString();
        
        assertNotNull(result);
        assertTrue(result.contains("Test Restaurant"));
        assertTrue(result.contains("123 Test St"));
        assertTrue(result.contains("open=true"));
    }

    @Test
    void should_validate_max_capacity_constraint() {
        // Given
        UUID id = UUID.randomUUID();
        String name = "Test";
        String address = "Address";
        LocalTime opening = LocalTime.of(9, 0);
        LocalTime closing = LocalTime.of(18, 0);
        int invalidCapacity = -5;

        // When & Then
        assertThrows(CapacitySlotValidationException.class,
            () -> new Restaurant(id, name, address, opening, closing, true, List.of(), new DeliverySchedule(id) {{
                {
                    generateDailySlots(LocalDate.now(), LocalTime.of(10, 0), LocalTime.of(12, 0), 10);
                }
                {
                    getSlotsForDate(LocalDate.now()).forEach(slot -> slot.setMaxCapacity(invalidCapacity));
                }
            }}));
    }

    @Test
    void should_validate_name_length_constraint() {
        String tooLongName = "a".repeat(201); // More than 200 characters
        String address = "Address";

        assertThrows(RestaurantValidationException.class,
            () -> new Restaurant(tooLongName, address));
    }

    @Test
    void should_not_equal_different_restaurants() {
        Restaurant restaurant1 = new Restaurant("Test1", "Address1");
        Restaurant restaurant2 = new Restaurant("Test2", "Address2");

        assertNotEquals(restaurant1, restaurant2);
        assertNotEquals(null, restaurant1);
    }
}

