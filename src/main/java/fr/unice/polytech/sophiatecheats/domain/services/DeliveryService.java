package fr.unice.polytech.sophiatecheats.domain.services;

import fr.unice.polytech.sophiatecheats.domain.entities.restaurant.TimeSlot;
import fr.unice.polytech.sophiatecheats.domain.exceptions.SlotNotFoundException;
import fr.unice.polytech.sophiatecheats.domain.repositories.TimeSlotRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Service métier gérant la logique de réservation et de disponibilité des créneaux de livraison.
 */
public class DeliveryService {

    private final TimeSlotRepository repository;

    public DeliveryService(TimeSlotRepository repository) {
        this.repository = repository;
    }

    public List<TimeSlot> getAvailableSlots(LocalDate date) {
        return repository.findAvailableSlots(date);
    }

    public void reserveSlot(UUID slotId) {
        TimeSlot slot = repository.findById(slotId)
                .orElseThrow(() -> new SlotNotFoundException("Créneau introuvable : " + slotId));

        slot.reserveOrThrow(); // Use the method that throws exceptions

        repository.update(slot);
    }

    public void releaseSlot(UUID slotId) {
        repository.findById(slotId).ifPresent(slot -> {
            slot.release();
            repository.update(slot);
        });
    }
}
