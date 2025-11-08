package fr.unice.polytech.sophiatecheats.domain.services;

import fr.unice.polytech.sophiatecheats.domain.entities.restaurant.TimeSlot;
import fr.unice.polytech.sophiatecheats.domain.exceptions.ValidationException;
import fr.unice.polytech.sophiatecheats.domain.exceptions.SlotNotFoundException;
import fr.unice.polytech.sophiatecheats.domain.repositories.TimeSlotRepository;
import fr.unice.polytech.sophiatecheats.infrastructure.repositories.memory.InMemoryTimeSlotRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DeliveryServiceTest {

    private TimeSlotRepository repository;
    private DeliveryService service;

    @BeforeEach
    void setUp() {
        repository = Mockito.mock(TimeSlotRepository.class);
        service = new DeliveryService(repository);
    }

    @Test
    void testGetAvailableSlotsReturnsListFromRepository() {
        LocalDate date = LocalDate.now();
        List<TimeSlot> fakeSlots = List.of(
                new TimeSlot(UUID.randomUUID(), LocalDateTime.now(), LocalDateTime.now().plusMinutes(30), 5)
        );

        when(repository.findAvailableSlots(date)).thenReturn(fakeSlots);

        List<TimeSlot> result = service.getAvailableSlots(date);

        verify(repository, times(1)).findAvailableSlots(date);
        assertEquals(1, result.size());
        assertEquals(fakeSlots.getFirst(), result.getFirst());
    }

    @Test
    void testReserveSlotSuccess() {
        UUID slotId = UUID.randomUUID();
        TimeSlot slot = new TimeSlot(UUID.randomUUID(), LocalDateTime.now(), LocalDateTime.now().plusMinutes(30), 2);

        when(repository.findById(slotId)).thenReturn(Optional.of(slot));

        service.reserveSlot(slotId);

        assertEquals(1, slot.getReservedCount());
        verify(repository).update(slot);
    }

    @Test
    void testReserveSlotThrowsIfFull() {
        UUID slotId = UUID.randomUUID();
        TimeSlot slot = new TimeSlot(UUID.randomUUID(), LocalDateTime.now(), LocalDateTime.now().plusMinutes(30), 1);
        slot.reserve(); // rempli

        when(repository.findById(slotId)).thenReturn(Optional.of(slot));

        assertThrows(ValidationException.class, () -> service.reserveSlot(slotId));
    }

    @Test
    void testReserveSlotThrowsIfNotFound() {
        when(repository.findById(any())).thenReturn(Optional.empty());
        assertThrows(SlotNotFoundException.class, () -> service.reserveSlot(UUID.randomUUID()));
    }

    @Test
    void testReleaseSlotSuccess() {
        UUID slotId = UUID.randomUUID();
        TimeSlot slot = new TimeSlot(UUID.randomUUID(), LocalDateTime.now(), LocalDateTime.now().plusMinutes(30), 3);
        slot.reserve();

        when(repository.findById(slotId)).thenReturn(Optional.of(slot));

        service.releaseSlot(slotId);

        assertEquals(0, slot.getReservedCount());
        verify(repository).update(slot);
    }

    @Test
    void testReleaseSlotNoErrorIfNotFound() {
        when(repository.findById(any())).thenReturn(Optional.empty());
        assertDoesNotThrow(() -> service.releaseSlot(UUID.randomUUID()));
    }

    @Test
    void testFullFlowReserveAndRelease() {
        InMemoryTimeSlotRepository repo = new InMemoryTimeSlotRepository();
        DeliveryService serviceLoc = new DeliveryService(repo);

        UUID restaurantId = UUID.randomUUID();
        TimeSlot slot = new TimeSlot(restaurantId, LocalDateTime.now().plusMinutes(5), LocalDateTime.now().plusMinutes(35), 2);
        repo.save(slot);

        serviceLoc.reserveSlot(slot.getId());
        assertEquals(1, slot.getReservedCount());

        serviceLoc.reserveSlot(slot.getId());
        assertEquals(2, slot.getReservedCount());

        // tente une réservation de trop
        assertThrows(ValidationException.class, () -> serviceLoc.reserveSlot(slot.getId()));

        serviceLoc.releaseSlot(slot.getId());
        assertEquals(1, slot.getReservedCount());
    }

}
