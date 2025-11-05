package fr.unice.polytech.sophiatecheats.domain.entities.restaurant;

import fr.unice.polytech.sophiatecheats.domain.exceptions.CapacitySlotValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class CapacitySlotTest {
    private UUID timeSlotId;
    private CapacitySlot capacitySlot;

    @BeforeEach
    void setUp() {
        timeSlotId = UUID.randomUUID();
        capacitySlot = new CapacitySlot(timeSlotId, 5);
    }

    @Test
    void should_create_valid_capacity_slot() {
        assertNotNull(capacitySlot.getId());
        assertEquals(timeSlotId, capacitySlot.getTimeSlotId());
        assertEquals(5, capacitySlot.getMaxCapacity());
        assertEquals(0, capacitySlot.getCurrentCapacity());
        assertTrue(capacitySlot.isActive());
        assertFalse(capacitySlot.isFull());
        assertEquals(5, capacitySlot.getAvailableSpots());
        assertTrue(capacitySlot.hasAvailableCapacity());
    }

    @Test
    void should_create_capacity_slot_with_full_constructor() {
        UUID id = UUID.randomUUID();
        CapacitySlot slot = new CapacitySlot(id, timeSlotId, 10, 5, true);

        assertEquals(id, slot.getId());
        assertEquals(timeSlotId, slot.getTimeSlotId());
        assertEquals(10, slot.getMaxCapacity());
        assertEquals(5, slot.getCurrentCapacity());
        assertTrue(slot.isActive());
        assertEquals(5, slot.getAvailableSpots());
        assertTrue(slot.hasAvailableCapacity());
        assertFalse(slot.isFull());
    }

    @Test
    void should_reserve_spot_successfully() {
        assertTrue(capacitySlot.reserve());
        assertEquals(1, capacitySlot.getCurrentCapacity());
        assertEquals(4, capacitySlot.getAvailableSpots());
        assertFalse(capacitySlot.isFull());
        assertTrue(capacitySlot.hasAvailableCapacity());
    }

    @Test
    void should_fail_reservation_when_full() {
        // Fill to capacity
        for (int i = 0; i < 5; i++) {
            assertTrue(capacitySlot.reserve());
        }

        // Try one more reservation
        assertFalse(capacitySlot.reserve());
        assertEquals(5, capacitySlot.getCurrentCapacity());
        assertEquals(0, capacitySlot.getAvailableSpots());
        assertTrue(capacitySlot.isFull());
        assertFalse(capacitySlot.hasAvailableCapacity());
    }

    @Test
    void should_fail_reservation_when_inactive() {
        capacitySlot.deactivate();
        assertFalse(capacitySlot.reserve());
        assertEquals(0, capacitySlot.getCurrentCapacity());
        assertFalse(capacitySlot.hasAvailableCapacity());
    }

    @Test
    void should_handle_multiple_reservations_and_releases() {
        // Multiple reservations
        assertTrue(capacitySlot.reserve());
        assertTrue(capacitySlot.reserve());
        assertEquals(2, capacitySlot.getCurrentCapacity());

        // Release one
        capacitySlot.release();
        assertEquals(1, capacitySlot.getCurrentCapacity());
        assertEquals(4, capacitySlot.getAvailableSpots());

        // Reserve again
        assertTrue(capacitySlot.reserve());
        assertEquals(2, capacitySlot.getCurrentCapacity());
    }

    @Test
    void should_not_release_when_empty() {
        capacitySlot.release();
        assertEquals(0, capacitySlot.getCurrentCapacity());
        assertEquals(5, capacitySlot.getAvailableSpots());
    }

    @Test
    void should_handle_activation_state_changes() {
        assertTrue(capacitySlot.isActive());
        assertTrue(capacitySlot.hasAvailableCapacity());

        capacitySlot.deactivate();
        assertFalse(capacitySlot.isActive());
        assertFalse(capacitySlot.hasAvailableCapacity());
        assertFalse(capacitySlot.reserve());

        capacitySlot.activate();
        assertTrue(capacitySlot.isActive());
        assertTrue(capacitySlot.hasAvailableCapacity());
        assertTrue(capacitySlot.reserve());
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1, -5})
    void should_throw_exception_for_invalid_max_capacity(int invalidCapacity) {
        assertThrows(CapacitySlotValidationException.class,
            () -> new CapacitySlot(timeSlotId, invalidCapacity));
    }

    @Test
    void should_throw_exception_for_null_timeSlotId() {
        assertThrows(CapacitySlotValidationException.class,
            () -> new CapacitySlot(null, 5));
    }

    @Test
    void should_throw_exception_for_null_id() {
        assertThrows(CapacitySlotValidationException.class,
            () -> new CapacitySlot(null, timeSlotId, 5, 0, true));
    }

    @Test
    void should_throw_exception_for_negative_current_capacity() {
        assertThrows(CapacitySlotValidationException.class,
            () -> new CapacitySlot(UUID.randomUUID(), timeSlotId, 5, -1, true));
    }

    @Test
    void should_throw_exception_when_current_exceeds_max_capacity() {
        assertThrows(CapacitySlotValidationException.class,
            () -> new CapacitySlot(UUID.randomUUID(), timeSlotId, 5, 6, true));
    }

    @Test
    void should_properly_implement_equals_and_hashCode() {
        // Same ID should be equal
        UUID sharedId = UUID.randomUUID();
        CapacitySlot slot1 = new CapacitySlot(sharedId, timeSlotId, 5, 0, true);
        CapacitySlot slot2 = new CapacitySlot(sharedId, UUID.randomUUID(), 10, 2, false);

        assertEquals(slot1.hashCode(), slot2.hashCode());

        // Different IDs should have different hash codes
        CapacitySlot slot3 = new CapacitySlot(timeSlotId, 5);
        assertNotEquals(slot1.hashCode(), slot3.hashCode());
    }

    @Test
    void should_generate_proper_string_representation() {
        capacitySlot.reserve();
        String result = capacitySlot.toString();

        assertTrue(result.contains(capacitySlot.getId().toString()));
        assertTrue(result.contains(timeSlotId.toString()));
        assertTrue(result.contains("capacity=1/5"));
        assertTrue(result.contains("active=true"));
    }

    @Test
    void should_maintain_correct_available_spots() {
        assertEquals(5, capacitySlot.getAvailableSpots());

        capacitySlot.reserve();
        assertEquals(4, capacitySlot.getAvailableSpots());

        capacitySlot.release();
        assertEquals(5, capacitySlot.getAvailableSpots());

        for (int i = 0; i < 5; i++) {
            capacitySlot.reserve();
        }
        assertEquals(0, capacitySlot.getAvailableSpots());
    }
}
