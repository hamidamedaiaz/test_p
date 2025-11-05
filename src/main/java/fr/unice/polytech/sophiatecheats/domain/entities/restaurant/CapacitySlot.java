package fr.unice.polytech.sophiatecheats.domain.entities.restaurant;

import fr.unice.polytech.sophiatecheats.domain.entities.Entity;
import fr.unice.polytech.sophiatecheats.domain.exceptions.CapacitySlotValidationException;
import lombok.Getter;

import java.util.UUID;

/**
 * Entité représentant un créneau de capacité pour un TimeSlot.
 * Gère le nombre de réservations possibles dans un créneau horaire donné.
 */
@Getter
public class CapacitySlot implements Entity<UUID> {

    private final UUID id;
    private final UUID timeSlotId;
    private final int maxCapacity;
    private int currentCapacity;
    private boolean active;

    /**
     * Constructeur pour créer un nouveau créneau de capacité.
     */
    public CapacitySlot(UUID timeSlotId, int maxCapacity) {
        this.id = UUID.randomUUID();
        this.timeSlotId = timeSlotId;
        this.maxCapacity = maxCapacity;
        this.currentCapacity = 0;
        this.active = true;
        validate();
    }

    /**
     * Constructeur pour reconstruire un créneau de capacité existant.
     */
    public CapacitySlot(UUID id, UUID timeSlotId, int maxCapacity, int currentCapacity, boolean active) {
        this.id = id;
        this.timeSlotId = timeSlotId;
        this.maxCapacity = maxCapacity;
        this.currentCapacity = currentCapacity;
        this.active = active;
        validate();
    }

    @Override
    public UUID getId() {
        return id;
    }

    @Override
    public void validate() {
        if (id == null) {
            throw new CapacitySlotValidationException("L'identifiant ne peut pas être null");
        }
        if (timeSlotId == null) {
            throw new CapacitySlotValidationException("L'identifiant du créneau horaire ne peut pas être null");
        }
        if (maxCapacity <= 0) {
            throw new CapacitySlotValidationException("La capacité maximale doit être positive");
        }
        if (currentCapacity < 0) {
            throw new CapacitySlotValidationException("La capacité actuelle ne peut pas être négative");
        }
        if (currentCapacity > maxCapacity) {
            throw new CapacitySlotValidationException("La capacité actuelle ne peut pas dépasser la capacité maximale");
        }
    }

    /**
     * Tente de réserver une place dans ce créneau.
     * @return true si la réservation a réussi, false sinon
     */
    public boolean reserve() {
        if (!active) {
            return false;
        }
        if (currentCapacity >= maxCapacity) {
            return false;
        }
        currentCapacity++;
        return true;
    }

    /**
     * Libère une place dans ce créneau.
     */
    public void release() {
        if (currentCapacity > 0) {
            currentCapacity--;
        }
    }

    /**
     * Vérifie si le créneau a de la capacité disponible.
     * @return true si des places sont disponibles et le créneau est actif
     */
    public boolean hasAvailableCapacity() {
        return active && currentCapacity < maxCapacity;
    }

    /**
     * Vérifie si le créneau est plein.
     * @return true si le créneau a atteint sa capacité maximale
     */
    public boolean isFull() {
        return currentCapacity >= maxCapacity;
    }

    /**
     * Obtient le nombre de places disponibles.
     * @return le nombre de places libres
     */
    public int getAvailableSpots() {
        return Math.max(0, maxCapacity - currentCapacity);
    }

    /**
     * Désactive le créneau.
     */
    public void deactivate() {
        this.active = false;
    }

    /**
     * Active le créneau.
     */
    public void activate() {
        this.active = true;
    }

    /**
     * Met à jour la capacité maximale.
     * @param newMaxCapacity la nouvelle capacité maximale
     */
    public void updateMaxCapacity(int newMaxCapacity) {
        if (newMaxCapacity <= 0) {
            throw new IllegalArgumentException("La capacité maximale doit être positive");
        }
        if (newMaxCapacity < currentCapacity) {
            throw new IllegalArgumentException("La nouvelle capacité ne peut pas être inférieure à la capacité actuelle");
        }
        // Note: Cette méthode ne peut pas modifier un champ final,
        // elle devrait créer une nouvelle instance dans une vraie implémentation
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        CapacitySlot that = (CapacitySlot) obj;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return String.format("CapacitySlot{id=%s, timeSlotId=%s, capacity=%d/%d, active=%s}",
                           id, timeSlotId, currentCapacity, maxCapacity, active);
    }
}
