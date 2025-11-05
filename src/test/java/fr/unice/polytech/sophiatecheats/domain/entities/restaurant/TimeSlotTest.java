package fr.unice.polytech.sophiatecheats.domain.entities.restaurant;

import com.ethlo.time.DateTime;
import fr.unice.polytech.sophiatecheats.domain.exceptions.CapacitySlotValidationException;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class TimeSlotTest {

    @Test
    void should_create_valid_timeSlot() {
        // Given
        UUID restaurantId = UUID.randomUUID();
        LocalDateTime fixedFutureDate = LocalDateTime.now()
                .plusDays(7)
                .withHour(12)
                .withMinute(0)
                .withSecond(0)
                .withNano(0);
        LocalTime startTime = LocalTime.of(12, 0);


        int maxCapacity = 10;

        // When
        TimeSlot timeSlot = new TimeSlot(restaurantId, startTime, fixedFutureDate, maxCapacity);

        // Then
        assertNotNull(timeSlot.getId());
        assertEquals(restaurantId, timeSlot.getRestaurantId());
        assertEquals(startTime, timeSlot.getStartTime());
        assertEquals(startTime.plusMinutes(30), timeSlot.getEndTime());
        assertEquals(fixedFutureDate, timeSlot.getDate());
        assertTrue(timeSlot.isAvailable());
        assertEquals(maxCapacity, timeSlot.getMaxCapacity());
        assertEquals(0, timeSlot.getCurrentCapacity());
        assertEquals(maxCapacity, timeSlot.getAvailableSpots());
    }

    @Test
    void should_create_timeSlot_with_full_constructor() {
        // Given
        UUID id = UUID.randomUUID();
        UUID restaurantId = UUID.randomUUID();
        LocalTime startTime = LocalTime.of(14, 0);
        LocalTime endTime = LocalTime.of(14, 30);
        LocalDateTime date = LocalDateTime.of(2025, 10, 15, 14, 0);
        boolean available = false;
        CapacitySlot capacitySlot = new CapacitySlot(id, 5);

        // When
        TimeSlot timeSlot = new TimeSlot(id, restaurantId, startTime, endTime, date, available, capacitySlot);

        // Then
        assertEquals(id, timeSlot.getId());
        assertEquals(restaurantId, timeSlot.getRestaurantId());
        assertEquals(startTime, timeSlot.getStartTime());
        assertEquals(endTime, timeSlot.getEndTime());
        assertEquals(date, timeSlot.getDate());
        assertFalse(timeSlot.isAvailable());
        assertEquals(capacitySlot, timeSlot.getCapacitySlot());
    }

    @Test
    void should_reserve_and_release_spots() {
        // Given
        UUID restaurantId = UUID.randomUUID();
        TimeSlot timeSlot = new TimeSlot(restaurantId, LocalTime.of(12, 0), 
                                       LocalDateTime.of(2030, 10, 15, 12, 0), 2);

        // When & Then
        assertTrue(timeSlot.reserve()); // First reservation
        assertEquals(1, timeSlot.getCurrentCapacity());
        assertEquals(1, timeSlot.getAvailableSpots());

        assertTrue(timeSlot.reserve()); // Second reservation
        assertEquals(2, timeSlot.getCurrentCapacity());
        assertEquals(0, timeSlot.getAvailableSpots());
        assertTrue(timeSlot.isFull());

        assertFalse(timeSlot.reserve()); // Cannot reserve when full

        // Release one spot
        timeSlot.release();
        assertEquals(1, timeSlot.getCurrentCapacity());
        assertFalse(timeSlot.isFull());
    }

    @Test
    void should_activate_and_deactivate() {
        // Given
        UUID restaurantId = UUID.randomUUID();
        TimeSlot timeSlot = new TimeSlot(restaurantId, LocalTime.of(12, 0), 
                                       LocalDateTime.of(2030, 10, 15, 12, 0), 5);

        // When
        timeSlot.deactivate();

        // Then
        assertFalse(timeSlot.isAvailable());
        assertFalse(timeSlot.reserve());

        // When
        timeSlot.activate();

        // Then
        assertTrue(timeSlot.isAvailable());
        assertTrue(timeSlot.reserve());
    }

    @Test
    void should_update_max_capacity() {
        // Given
        UUID restaurantId = UUID.randomUUID();
        TimeSlot timeSlot = new TimeSlot(restaurantId, LocalTime.of(12, 0), 
                                       LocalDateTime.of(2030, 10, 15, 12, 0), 5);
        timeSlot.reserve();
        timeSlot.reserve();

        // When
        timeSlot.updateMaxCapacity(10);

        // Then
        assertEquals(10, timeSlot.getMaxCapacity());
        // updateMaxCapacity crée un nouveau CapacitySlot, donc currentCapacity redevient 0
        assertEquals(0, timeSlot.getCurrentCapacity());
        assertEquals(10, timeSlot.getAvailableSpots());
    }

    @Test
    void should_fail_updating_capacity_below_current() {
        // Given
        UUID restaurantId = UUID.randomUUID();
        TimeSlot timeSlot = new TimeSlot(restaurantId, LocalTime.of(12, 0), 
                                       LocalDateTime.of(2030, 10, 15, 12, 0), 5);
        timeSlot.reserve();
        timeSlot.reserve();

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> timeSlot.updateMaxCapacity(1));
        assertThrows(IllegalArgumentException.class, () -> timeSlot.updateMaxCapacity(0));
        assertThrows(IllegalArgumentException.class, () -> timeSlot.updateMaxCapacity(-1));
    }

    @Test
    void should_get_delivery_time() {
        // Given
        UUID restaurantId = UUID.randomUUID();
        TimeSlot timeSlot = new TimeSlot(restaurantId, LocalTime.of(12, 0), 
                                       LocalDateTime.of(2025, 10, 15, 12, 0), 5);

        // When
        LocalTime deliveryTime = timeSlot.getDeliveryTime();

        // Then
        assertEquals(LocalTime.of(12, 45), deliveryTime); // 12:30 + 15 minutes
    }

    @Test
    void should_check_if_past() {
        // Given - Past slot
        UUID restaurantId = UUID.randomUUID();
        TimeSlot pastSlot = new TimeSlot(restaurantId, LocalTime.of(10, 0), 
                                       LocalDateTime.of(2020, 1, 1, 10, 0), 5);

        // Given - Future slot
        TimeSlot futureSlot = new TimeSlot(restaurantId, LocalTime.of(10, 0), 
                                         LocalDateTime.of(2030, 12, 31, 10, 0), 5);

        // Then
        assertTrue(pastSlot.isPast());
        assertFalse(futureSlot.isPast());
    }

    @Test
    void should_fail_validation_with_null_values() {
        // Given
        UUID restaurantId = UUID.randomUUID();
        UUID id = UUID.randomUUID();
        LocalTime time = LocalTime.of(12, 0);
        LocalDateTime date = LocalDateTime.of(2025, 10, 15, 12, 0);
        CapacitySlot capacity = new CapacitySlot(id, 5);

        // When & Then
        assertThrows(CapacitySlotValidationException.class, 
            () -> new TimeSlot(null, time, date, 5)); // null restaurant ID

        assertThrows(NullPointerException.class, 
            () -> new TimeSlot(restaurantId, null, date, 5)); // null start time causes NPE before validation

        assertThrows(CapacitySlotValidationException.class, 
            () -> new TimeSlot(restaurantId, time, null, 5)); // null date

        assertThrows(CapacitySlotValidationException.class, 
            () -> new TimeSlot(id, restaurantId, null, time, date, true, capacity)); // null start time

        assertThrows(CapacitySlotValidationException.class, 
            () -> new TimeSlot(id, restaurantId, time, null, date, true, capacity)); // null end time
    }

    @Test
    void should_fail_when_start_after_end() {
        // Given
        UUID id = UUID.randomUUID();
        UUID restaurantId = UUID.randomUUID();
        LocalTime startTime = LocalTime.of(14, 0);
        LocalTime endTime = LocalTime.of(12, 0); // Before start time
        LocalDateTime date = LocalDateTime.of(2025, 10, 15, 12, 0);
        CapacitySlot capacity = new CapacitySlot(id, 5);

        // When & Then
        assertThrows(CapacitySlotValidationException.class, 
            () -> new TimeSlot(id, restaurantId, startTime, endTime, date, true, capacity));
    }

    @Test
    void should_have_meaningful_toString() {
        // Given
        UUID restaurantId = UUID.randomUUID();
        TimeSlot timeSlot = new TimeSlot(restaurantId, LocalTime.of(12, 0), 
                                       LocalDateTime.of(2025, 10, 15, 12, 0), 5);

        // When
        String result = timeSlot.toString();

        // Then
        assertNotNull(result);
        assertTrue(result.contains("TimeSlot"));
        assertTrue(result.contains("12:00"));
        assertTrue(result.contains("12:30"));
        assertTrue(result.contains("2025-10-15"));
        assertTrue(result.contains("available=true"));
        assertTrue(result.contains("capacity=0/5"));
    }

    @Test
    void should_handle_null_capacity_slot() {
        // Given
        UUID id = UUID.randomUUID();
        UUID restaurantId = UUID.randomUUID();
        LocalTime startTime = LocalTime.of(12, 0);
        LocalTime endTime = LocalTime.of(12, 30);
        LocalDateTime date = LocalDateTime.of(2025, 10, 15, 12, 0);

        // When
        TimeSlot timeSlot = new TimeSlot(id, restaurantId, startTime, endTime, date, true, null);

        // Then
        assertFalse(timeSlot.reserve());
        assertEquals(0, timeSlot.getAvailableSpots());
        assertEquals(0, timeSlot.getMaxCapacity());
        assertEquals(0, timeSlot.getCurrentCapacity());
        assertFalse(timeSlot.isFull());
        
        // Release should not fail with null capacity
        timeSlot.release();
    }
}