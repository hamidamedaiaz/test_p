package fr.unice.polytech.sophiatecheats.domain.entities.restaurant;

import fr.unice.polytech.sophiatecheats.domain.exceptions.ValidationException;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class TimeSlotTest {

    @Test
    void shouldCreateTimeSlotUsingBuilder() {
        UUID restaurantId = UUID.randomUUID();
        LocalDateTime startTime = LocalDateTime.now().plusDays(1).withHour(12).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime endTime = startTime.plusMinutes(30);

        TimeSlot timeSlot = TimeSlot.builder()
                .restaurantId(restaurantId)
                .startTime(startTime)
                .endTime(endTime)
                .maxCapacity(10)
                .build();

        assertNotNull(timeSlot.getId());
        assertEquals(restaurantId, timeSlot.getRestaurantId());
        assertEquals(startTime, timeSlot.getStartTime());
        assertEquals(endTime, timeSlot.getEndTime());
        assertEquals(10, timeSlot.getMaxCapacity());
        assertEquals(0, timeSlot.getReservedCount());
        assertTrue(timeSlot.isAvailable());
    }

    @Test
    void shouldCreateTimeSlotWithBuilderAndAllParameters() {
        UUID id = UUID.randomUUID();
        UUID restaurantId = UUID.randomUUID();
        LocalDateTime startTime = LocalDateTime.now().plusDays(1).withHour(14).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime endTime = startTime.plusMinutes(30);

        TimeSlot timeSlot = TimeSlot.builder()
                .id(id)
                .restaurantId(restaurantId)
                .startTime(startTime)
                .endTime(endTime)
                .maxCapacity(15)
                .reservedCount(5)
                .available(true)
                .build();

        assertEquals(id, timeSlot.getId());
        assertEquals(restaurantId, timeSlot.getRestaurantId());
        assertEquals(startTime, timeSlot.getStartTime());
        assertEquals(endTime, timeSlot.getEndTime());
        assertEquals(15, timeSlot.getMaxCapacity());
        assertEquals(5, timeSlot.getReservedCount());
        assertTrue(timeSlot.isAvailable());
    }

    @Test
    void shouldCreateTimeSlotUsingBuilderWithLocalTime() {
        UUID restaurantId = UUID.randomUUID();
        LocalTime time = LocalTime.of(10, 30);
        LocalDateTime date = LocalDateTime.now().plusDays(1);

        TimeSlot timeSlot = TimeSlot.builder()
                .restaurantId(restaurantId)
                .startTime(time, date)
                .endTime(time.plusMinutes(30), date)
                .maxCapacity(8)
                .build();

        assertNotNull(timeSlot.getId());
        assertEquals(restaurantId, timeSlot.getRestaurantId());
        assertEquals(time, timeSlot.getStartTimeAsLocalTime());
        assertEquals(time.plusMinutes(30), timeSlot.getEndTimeAsLocalTime());
        assertEquals(8, timeSlot.getMaxCapacity());
    }

    @Test
    void shouldCreateThirtyMinuteSlotUsingBuilder() {
        UUID restaurantId = UUID.randomUUID();
        LocalTime startTime = LocalTime.of(15, 0);
        LocalDateTime date = LocalDateTime.now().plusDays(1);

        TimeSlot timeSlot = TimeSlot.builder()
                .restaurantId(restaurantId)
                .thirtyMinuteSlot(startTime, date)
                .maxCapacity(12)
                .build();

        assertNotNull(timeSlot.getId());
        assertEquals(restaurantId, timeSlot.getRestaurantId());
        assertEquals(startTime, timeSlot.getStartTimeAsLocalTime());
        assertEquals(startTime.plusMinutes(30), timeSlot.getEndTimeAsLocalTime());
        assertEquals(12, timeSlot.getMaxCapacity());
    }

    @Test
    void shouldGenerateUuidWhenNotProvidedInBuilder() {
        UUID restaurantId = UUID.randomUUID();
        LocalDateTime startTime = LocalDateTime.now().plusDays(1).withHour(11).withMinute(0).withSecond(0).withNano(0);

        TimeSlot timeSlot1 = TimeSlot.builder()
                .restaurantId(restaurantId)
                .startTime(startTime)
                .endTime(startTime.plusMinutes(30))
                .maxCapacity(5)
                .build();

        TimeSlot timeSlot2 = TimeSlot.builder()
                .restaurantId(restaurantId)
                .startTime(startTime.plusHours(1))
                .endTime(startTime.plusHours(1).plusMinutes(30))
                .maxCapacity(5)
                .build();

        assertNotNull(timeSlot1.getId());
        assertNotNull(timeSlot2.getId());
        assertNotEquals(timeSlot1.getId(), timeSlot2.getId());
    }

    @Test
    void shouldCreateTimeSlotWithBuilderAndDefaultValues() {
        UUID restaurantId = UUID.randomUUID();
        LocalDateTime startTime = LocalDateTime.now().plusDays(1).withHour(9).withMinute(0).withSecond(0).withNano(0);

        TimeSlot timeSlot = TimeSlot.builder()
                .restaurantId(restaurantId)
                .startTime(startTime)
                .endTime(startTime.plusMinutes(30))
                .maxCapacity(10)
                .build();

        assertEquals(0, timeSlot.getReservedCount()); // Default value
        assertTrue(timeSlot.isAvailable()); // Default value
    }

    @Test
    void shouldCreateUnavailableTimeSlotUsingBuilder() {
        UUID restaurantId = UUID.randomUUID();
        LocalDateTime startTime = LocalDateTime.now().plusDays(1).withHour(16).withMinute(0).withSecond(0).withNano(0);

        TimeSlot timeSlot = TimeSlot.builder()
                .restaurantId(restaurantId)
                .startTime(startTime)
                .endTime(startTime.plusMinutes(30))
                .maxCapacity(10)
                .available(false)
                .build();

        assertFalse(timeSlot.isAvailable());
    }

    @Test
    void shouldCreatePartiallyReservedTimeSlotUsingBuilder() {
        UUID restaurantId = UUID.randomUUID();
        LocalDateTime startTime = LocalDateTime.now().plusDays(1).withHour(13).withMinute(30).withSecond(0).withNano(0);

        TimeSlot timeSlot = TimeSlot.builder()
                .restaurantId(restaurantId)
                .startTime(startTime)
                .endTime(startTime.plusMinutes(30))
                .maxCapacity(10)
                .reservedCount(7)
                .build();

        assertEquals(7, timeSlot.getReservedCount());
        assertEquals(3, timeSlot.getAvailableSpots());
        assertFalse(timeSlot.isFull());
    }

    @Test
    void shouldThrowValidationErrorWhenBuildingWithoutRestaurantId() {
        LocalDateTime startTime = LocalDateTime.now().plusDays(1).withHour(12).withMinute(0).withSecond(0).withNano(0);

        assertThrows(ValidationException.class, () ->
                TimeSlot.builder()
                        .startTime(startTime)
                        .endTime(startTime.plusMinutes(30))
                        .maxCapacity(10)
                        .build()
        );
    }

    @Test
    void shouldThrowValidationErrorWhenBuildingWithInvalidCapacity() {
        UUID restaurantId = UUID.randomUUID();
        LocalDateTime startTime = LocalDateTime.now().plusDays(1).withHour(12).withMinute(0).withSecond(0).withNano(0);

        assertThrows(ValidationException.class, () ->
                TimeSlot.builder()
                        .restaurantId(restaurantId)
                        .startTime(startTime)
                        .endTime(startTime.plusMinutes(30))
                        .maxCapacity(0)
                        .build()
        );
    }

    @Test
    void shouldCreateTimeSlotWithBuilderFluentInterface() {
        // Démonstration de l'interface fluide du Builder
        UUID restaurantId = UUID.randomUUID();
        LocalTime startTime = LocalTime.of(18, 0);
        LocalDateTime date = LocalDateTime.now().plusDays(2);

        TimeSlot timeSlot = TimeSlot.builder()
                .restaurantId(restaurantId)
                .thirtyMinuteSlot(startTime, date)
                .maxCapacity(20)
                .reservedCount(10)
                .available(true)
                .build();

        assertEquals(restaurantId, timeSlot.getRestaurantId());
        assertEquals(20, timeSlot.getMaxCapacity());
        assertEquals(10, timeSlot.getReservedCount());
        assertTrue(timeSlot.isAvailable());
    }

    @Test
    void should_create_valid_time_slot() {
        // Given
        UUID restaurantId = UUID.randomUUID();
        LocalTime startTime = LocalTime.of(12, 0);
        LocalDateTime date = LocalDateTime.now().plusDays(1);
        int maxCapacity = 5;

        // When
        TimeSlot timeSlot = new TimeSlot(restaurantId, startTime, date, maxCapacity);

        // Then
        assertNotNull(timeSlot.getId());
        assertEquals(restaurantId, timeSlot.getRestaurantId());
        assertEquals(startTime, timeSlot.getStartTimeAsLocalTime()); // Use correct getter
        assertEquals(startTime.plusMinutes(30), timeSlot.getEndTimeAsLocalTime()); // Use correct getter
        assertEquals(date.toLocalDate(), timeSlot.getStartTime().toLocalDate());
        assertTrue(timeSlot.isAvailable());
        assertEquals(maxCapacity, timeSlot.getMaxCapacity());
        assertEquals(0, timeSlot.getCurrentCapacity());
        assertEquals(LocalTime.of(12, 45), timeSlot.getDeliveryTime());
    }

    @Test
    void should_create_time_slot_with_full_constructor() {
        // Given
        UUID id = UUID.randomUUID();
        UUID restaurantId = UUID.randomUUID();
        LocalDateTime startDateTime = LocalDateTime.now().plusDays(1).withHour(14).withMinute(0);
        LocalDateTime endDateTime = startDateTime.plusMinutes(30);
        int maxCapacity = 10;

        // When
        TimeSlot timeSlot = new TimeSlot(id, restaurantId, startDateTime, endDateTime, maxCapacity);

        // Then
        assertEquals(id, timeSlot.getId());
        assertEquals(restaurantId, timeSlot.getRestaurantId());
        assertEquals(startDateTime.toLocalTime(), timeSlot.getStartTimeAsLocalTime());
        assertEquals(endDateTime.toLocalTime(), timeSlot.getEndTimeAsLocalTime());
        assertEquals(startDateTime.toLocalDate(), timeSlot.getStartTime().toLocalDate());
        assertTrue(timeSlot.isAvailable());
        assertEquals(maxCapacity, timeSlot.getMaxCapacity());
        assertEquals(0, timeSlot.getCurrentCapacity());
    }

    @Test
    void should_throw_exception_when_restaurant_id_is_null() {
        // Given
        LocalTime startTime = LocalTime.of(12, 0);
        LocalDateTime date = LocalDateTime.now().plusDays(1);
        int maxCapacity = 5;

        // When & Then
        assertThrows(ValidationException.class, () ->
            new TimeSlot(null, startTime, date, maxCapacity));
    }

    @Test
    void should_throw_exception_when_start_time_is_null() {
        // Given
        UUID restaurantId = UUID.randomUUID();
        LocalDateTime date = LocalDateTime.now().plusDays(1);
        int maxCapacity = 5;

        // When & Then
        assertThrows(ValidationException.class, () ->
            new TimeSlot(restaurantId, (LocalTime) null, date, maxCapacity));
    }

    @Test
    void should_throw_exception_when_date_is_null() {
        // Given
        UUID restaurantId = UUID.randomUUID();
        LocalTime startTime = LocalTime.of(12, 0);
        int maxCapacity = 5;

        // When & Then
        assertThrows(ValidationException.class, () ->
            new TimeSlot(restaurantId, startTime, null, maxCapacity));
    }

    @Test
    void should_throw_exception_when_max_capacity_is_zero_or_negative() {
        // Given
        UUID restaurantId = UUID.randomUUID();
        LocalTime startTime = LocalTime.of(12, 0);
        LocalDateTime date = LocalDateTime.now().plusDays(1);

        // When & Then
        assertThrows(ValidationException.class, () ->
            new TimeSlot(restaurantId, startTime, date, 0));
        assertThrows(ValidationException.class, () ->
            new TimeSlot(restaurantId, startTime, date, -1));
    }

    @Test
    void should_reserve_slot_successfully_when_available() {
        // Given
        UUID restaurantId = UUID.randomUUID();
        LocalTime startTime = LocalTime.of(12, 0);
        LocalDateTime date = LocalDateTime.now().plusDays(1);
        TimeSlot timeSlot = new TimeSlot(restaurantId, startTime, date, 5);

        // When
        boolean result = timeSlot.reserve();

        // Then
        assertTrue(result);
        assertEquals(1, timeSlot.getCurrentCapacity());
        assertTrue(timeSlot.isAvailable());
    }

    @Test
    void should_become_full_when_max_capacity_reached() {
        // Given
        UUID restaurantId = UUID.randomUUID();
        LocalTime startTime = LocalTime.of(12, 0);
        LocalDateTime date = LocalDateTime.now().plusDays(1);
        TimeSlot timeSlot = new TimeSlot(restaurantId, startTime, date, 2);

        // When
        timeSlot.reserve();
        timeSlot.reserve();

        // Then
        assertEquals(2, timeSlot.getCurrentCapacity());
        assertTrue(timeSlot.isFull());
        assertFalse(timeSlot.getAvailableSpots() > 0);
        assertEquals(0, timeSlot.getAvailableSpots());
    }

    @Test
    void should_not_reserve_when_full() {
        // Given
        UUID restaurantId = UUID.randomUUID();
        LocalTime startTime = LocalTime.of(12, 0);
        LocalDateTime date = LocalDateTime.now().plusDays(1);
        TimeSlot timeSlot = new TimeSlot(restaurantId, startTime, date, 1);
        timeSlot.reserve(); // Fill the slot

        // When
        boolean result = timeSlot.reserve();

        // Then
        assertFalse(result);
        assertEquals(1, timeSlot.getCurrentCapacity());
        assertTrue(timeSlot.isFull());
    }

    @Test
    void should_release_slot_successfully() {
        // Given
        UUID restaurantId = UUID.randomUUID();
        LocalTime startTime = LocalTime.of(12, 0);
        LocalDateTime date = LocalDateTime.now().plusDays(1);
        TimeSlot timeSlot = new TimeSlot(restaurantId, startTime, date, 5);
        timeSlot.reserve();

        // When
        timeSlot.release();

        // Then
        assertEquals(0, timeSlot.getCurrentCapacity());
        assertTrue(timeSlot.getAvailableSpots() > 0);
        assertFalse(timeSlot.isFull());
    }

    @Test
    void should_not_be_available_when_deactivated() {
        // Given
        UUID restaurantId = UUID.randomUUID();
        LocalTime startTime = LocalTime.of(12, 0);
        LocalDateTime date = LocalDateTime.now().plusDays(1);
        TimeSlot timeSlot = new TimeSlot(restaurantId, startTime, date, 5);

        // When
        timeSlot.deactivate();

        // Then
        assertFalse(timeSlot.isAvailable());
    }

    @Test
    void should_be_available_when_activated() {
        // Given
        UUID restaurantId = UUID.randomUUID();
        LocalTime startTime = LocalTime.of(12, 0);
        LocalDateTime date = LocalDateTime.now().plusDays(1);
        TimeSlot timeSlot = new TimeSlot(restaurantId, startTime, date, 5);
        timeSlot.deactivate();

        // When
        timeSlot.activate();

        // Then
        assertTrue(timeSlot.isAvailable());
    }

    @Test
    void should_calculate_delivery_time_correctly() {
        // Given
        UUID restaurantId = UUID.randomUUID();
        LocalTime startTime = LocalTime.of(14, 30);
        LocalDateTime date = LocalDateTime.now().plusDays(1);
        TimeSlot timeSlot = new TimeSlot(restaurantId, startTime, date, 5);

        // When
        LocalTime deliveryTime = timeSlot.getDeliveryTime();

        // Then
        assertEquals(LocalTime.of(15, 15), deliveryTime); // 14:30 + 30min + 15min = 15:15
    }

    @Test
    void should_have_meaningful_toString() {
        // Given
        UUID restaurantId = UUID.randomUUID();
        LocalTime startTime = LocalTime.of(12, 0);
        LocalDateTime date = LocalDateTime.of(2025, 10, 15, 12, 0);
        TimeSlot timeSlot = new TimeSlot(restaurantId, startTime, date, 5);

        // When
        String result = timeSlot.toString();

        // Then
        assertNotNull(result);
        assertTrue(result.contains("TimeSlot"));
        assertTrue(result.contains("12:00"));
        assertTrue(result.contains("12:30"));
        assertTrue(result.contains("0/5")); // current/max capacity
        assertTrue(result.contains("available=true"));
    }
}
