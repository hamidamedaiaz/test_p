package fr.unice.polytech.sophiatecheats.infrastructure.repositories.memory;
import fr.unice.polytech.sophiatecheats.domain.entities.restaurant.TimeSlot;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;
class InMemoryTimeSlotRepositoryTest {
    private InMemoryTimeSlotRepository repository;
    private UUID restaurantId;
    private TimeSlot timeSlot;
    @BeforeEach
    void setUp() {
        repository = new InMemoryTimeSlotRepository();
        restaurantId = UUID.randomUUID();
        timeSlot = TimeSlot.builder()
                .restaurantId(restaurantId)
                .startTime(LocalDateTime.now().plusHours(1))
                .endTime(LocalDateTime.now().plusHours(1).plusMinutes(30))
                .maxCapacity(10)
                .build();
    }
    @Test
    void should_save_and_find_timeslot() {
        TimeSlot savedSlot = repository.save(timeSlot);
        assertNotNull(savedSlot);
        assertEquals(timeSlot.getId(), savedSlot.getId());
        Optional<TimeSlot> foundSlot = repository.findById(timeSlot.getId());
        assertTrue(foundSlot.isPresent());
        assertEquals(timeSlot.getId(), foundSlot.get().getId());
    }
    @Test
    void should_find_available_slots_by_date() {
        LocalDate date = LocalDate.now().plusDays(1);
        TimeSlot slot = TimeSlot.builder()
                .restaurantId(restaurantId)
                .startTime(date.atTime(12, 0))
                .endTime(date.atTime(12, 30))
                .maxCapacity(10)
                .build();
        repository.save(slot);
        List<TimeSlot> availableSlots = repository.findAvailableSlots(date);
        assertFalse(availableSlots.isEmpty());
        assertTrue(availableSlots.stream().anyMatch(s -> s.getId().equals(slot.getId())));
    }
    @Test
    void should_update_timeslot() {
        repository.save(timeSlot);
        timeSlot.setAvailable(false);
        repository.update(timeSlot);
        Optional<TimeSlot> updated = repository.findById(timeSlot.getId());
        assertTrue(updated.isPresent());
        assertFalse(updated.get().isAvailable());
    }
    @Test
    void should_delete_timeslot() {
        repository.save(timeSlot);
        boolean deleted = repository.deleteById(timeSlot.getId());
        assertTrue(deleted);
        Optional<TimeSlot> found = repository.findById(timeSlot.getId());
        assertFalse(found.isPresent());
    }
    @Test
    void should_check_if_timeslot_exists() {
        repository.save(timeSlot);
        assertTrue(repository.existsById(timeSlot.getId()));
        assertFalse(repository.existsById(UUID.randomUUID()));
    }
    @Test
    void should_find_all_timeslots() {
        TimeSlot slot1 = TimeSlot.builder()
                .restaurantId(restaurantId)
                .startTime(LocalDateTime.now().plusHours(1))
                .endTime(LocalDateTime.now().plusHours(1).plusMinutes(30))
                .maxCapacity(10)
                .build();
        TimeSlot slot2 = TimeSlot.builder()
                .restaurantId(restaurantId)
                .startTime(LocalDateTime.now().plusHours(2))
                .endTime(LocalDateTime.now().plusHours(2).plusMinutes(30))
                .maxCapacity(10)
                .build();
        repository.save(slot1);
        repository.save(slot2);
        List<TimeSlot> allSlots = repository.findAll();
        assertEquals(2, allSlots.size());
    }
    @Test
    void should_return_empty_when_timeslot_not_found() {
        Optional<TimeSlot> result = repository.findById(UUID.randomUUID());
        assertTrue(result.isEmpty());
    }
    @Test
    void should_return_false_when_deleting_non_existent_timeslot() {
        boolean deleted = repository.deleteById(UUID.randomUUID());
        assertFalse(deleted);
    }
    @Test
    void should_return_empty_list_when_no_available_slots_for_date() {
        LocalDate date = LocalDate.now().plusDays(10);
        List<TimeSlot> slots = repository.findAvailableSlots(date);
        assertTrue(slots.isEmpty());
    }
}
