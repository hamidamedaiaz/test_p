package fr.unice.polytech.sophiatecheats.infrastructure.repositories.memory;

import fr.unice.polytech.sophiatecheats.domain.entities.restaurant.TimeSlot;
import fr.unice.polytech.sophiatecheats.domain.repositories.TimeSlotRepository;

import java.time.LocalDate;
import java.util.*;

/**
 * Implémentation en mémoire du dépôt de créneaux de livraison.
 */
public class InMemoryTimeSlotRepository implements TimeSlotRepository {

    private final Map<UUID, TimeSlot> storage = new HashMap<>();

    @Override
    public List<TimeSlot> findAvailableSlots(LocalDate date) {
        return storage.values().stream()
                .filter(slot -> slot.getStartTime().toLocalDate().equals(date))
                .filter(TimeSlot::isAvailable)
                .toList();
    }

    @Override
    public TimeSlot save(TimeSlot slot) {
        storage.put(slot.getId(), slot);
        return slot;
    }

    @Override
    public void update(TimeSlot slot) {
        storage.put(slot.getId(), slot);
    }

    @Override
    public Optional<TimeSlot> findById(UUID id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public List<TimeSlot> findAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public boolean deleteById(UUID id) {
        return storage.remove(id) != null;
    }

    @Override
    public boolean existsById(UUID id) {
        return storage.containsKey(id);
    }
}
