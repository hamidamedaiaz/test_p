package fr.unice.polytech.sophiatecheats.domain.entities.restaurant;

import fr.unice.polytech.sophiatecheats.domain.entities.Entity;
import fr.unice.polytech.sophiatecheats.domain.exceptions.CapacitySlotValidationException;
import fr.unice.polytech.sophiatecheats.domain.exceptions.DomainException;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Entité représentant un créneau horaire de 30 minutes pour un restaurant.
 */
@Getter
@Setter
public class TimeSlot implements Entity<UUID> {

    private static final int SLOT_DURATION_MINUTES = 30;

    private final UUID id;
    private final UUID restaurantId;
    private final LocalTime startTime;
    private final LocalTime endTime;
    private final LocalDateTime date;
    private boolean available;
    private CapacitySlot capacitySlot;


    public TimeSlot(UUID restaurantId, LocalTime startTime, LocalDateTime date, int maxCapacity) {
        this.id = UUID.randomUUID();
        this.restaurantId = restaurantId;
        this.startTime = startTime;
        this.endTime = startTime.plusMinutes(SLOT_DURATION_MINUTES);
        this.date = date;
        this.available = true;
        this.capacitySlot = new CapacitySlot(this.id, maxCapacity);
        validate();
    }


    public TimeSlot(UUID id, UUID restaurantId, LocalTime startTime, LocalTime endTime,
                   LocalDateTime date, boolean available, CapacitySlot capacitySlot) {
        this.id = id;
        this.restaurantId = restaurantId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.date = date;
        this.available = available;
        this.capacitySlot = capacitySlot;
        validate();
    }


    public UUID getId() {
        return id;
    }


    public void validate() {
        if (restaurantId == null) {
            throw new CapacitySlotValidationException("L'identifiant du restaurant ne peut pas être null");
        }
        if (startTime == null) {
            throw new CapacitySlotValidationException("L'heure de début ne peut pas être null");
        }
        if (endTime == null) {
            throw new CapacitySlotValidationException("L'heure de fin ne peut pas être null");
        }
        if (date == null) {
            throw new CapacitySlotValidationException("La date ne peut pas être null");
        }
        if (startTime.isAfter(endTime)) {
            throw new CapacitySlotValidationException("L'heure de début ne peut pas être après l'heure de fin");
        }
        if (id == null) {
            throw new CapacitySlotValidationException("L'identifiant ne peut pas être null");
        }
    }

    /**
     * Tente de réserver une place dans ce créneau.
     * @return true si la réservation a réussi
     */
    public boolean reserve() {
        if (!available) {
            return false;
        }
        if (capacitySlot == null) {
            return false;
        }
        return capacitySlot.reserve();
    }

    /**
     * Libère une place dans ce créneau.
     */
    public void release() {
        if (capacitySlot != null) {
            capacitySlot.release();
        }
    }

    /**
     * Vérifie si le créneau est disponible pour réservation.
     * @return true si le créneau peut accepter de nouvelles commandes
     */
    public boolean isAvailable() {
        return available &&
               capacitySlot != null &&
               capacitySlot.hasAvailableCapacity() &&
             !isPast();
    }

    /**
     * Vérifie si le créneau est dans le passé.
     * @return true si le créneau est déjà passé
     */
    public boolean isPast() {
        LocalDateTime slotDateTime = date.with(startTime);
        return slotDateTime.isBefore(LocalDateTime.now());
    }

    /**
     * Vérifie si le créneau est plein.
     * @return true si le créneau a atteint sa capacité maximale
     */
    public boolean isFull() {
        return capacitySlot != null && capacitySlot.isFull();
    }

    /**
     * Obtient le nombre de places disponibles.
     * @return le nombre de places libres
     */
    public int getAvailableSpots() {
        return capacitySlot != null ? capacitySlot.getAvailableSpots() : 0;
    }

    /**
     * Obtient la capacité maximale du créneau.
     * @return la capacité maximale
     */
    public int getMaxCapacity() {
        return capacitySlot != null ? capacitySlot.getMaxCapacity() : 0;
    }

    /**
     * Obtient la capacité actuelle utilisée.
     * @return le nombre de places occupées
     */
    public int getCurrentCapacity() {
        return capacitySlot != null ? capacitySlot.getCurrentCapacity() : 0;
    }

    /**
     * Désactive le créneau.
     */
    public void deactivate() {
        this.available = false;
        if (capacitySlot != null) {
            capacitySlot.deactivate();
        }
    }

    /**
     * Active le créneau.
     */
    public void activate() {
        this.available = true;
        if (capacitySlot != null) {
            capacitySlot.activate();
        }
    }

    /**
     * Met à jour la capacité maximale du créneau.
     * @param newMaxCapacity la nouvelle capacité maximale
     */
    public void updateMaxCapacity(int newMaxCapacity) {
        if (newMaxCapacity <= 0) {
            throw new IllegalArgumentException("La capacité maximale doit être positive");
        }
        if (capacitySlot != null && newMaxCapacity < capacitySlot.getCurrentCapacity()) {
            throw new IllegalArgumentException("La nouvelle capacité ne peut pas être inférieure à la capacité actuelle utilisée");
        }
        this.capacitySlot = new CapacitySlot(this.id, newMaxCapacity);
    }

    /**
     * Obtient l'heure de fin de livraison (startTime + 15 minutes).
     * @return l'heure de livraison
     */
    public LocalTime getDeliveryTime() {
        return endTime.plusMinutes(15);
    }



    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("TimeSlot{id=%s, restaurantId=%s, date=%s, time=%s-%s, available=%s, capacity=%d/%d}",
                           id, restaurantId, date.toLocalDate(), startTime, endTime,
                           available, getCurrentCapacity(), getMaxCapacity());
    }


}
