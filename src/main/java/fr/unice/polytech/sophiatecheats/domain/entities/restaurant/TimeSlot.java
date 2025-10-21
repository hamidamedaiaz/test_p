package fr.unice.polytech.sophiatecheats.domain.entities.restaurant;

import fr.unice.polytech.sophiatecheats.domain.entities.Entity;
import fr.unice.polytech.sophiatecheats.domain.exceptions.ValidationException;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Représente un créneau de livraison de 30 minutes pour les commandes restaurant.
 *
 * <p>Cette classe implémente la logique métier des créneaux de livraison selon les
 * spécifications du système SophiaTech Eats:</p>
 *
 * <h3>Règles métier:</h3>
 * <ul>
 *   <li>Durée fixe de 30 minutes</li>
 *   <li>Capacité limitée par nombre de commandes</li>
 *   <li>Temps de livraison: fin du créneau + 15 minutes</li>
 *   <li>Réservation atomique avec gestion de la concurrence</li>
 *   <li>Désactivation automatique des créneaux passés</li>
 * </ul>
 *
 * <h3>États possibles:</h3>
 * <ul>
 *   <li><strong>Disponible:</strong> Des places libres et créneau futur</li>
 *   <li><strong>Plein:</strong> Capacité maximale atteinte</li>
 *   <li><strong>Passé:</strong> Créneau expiré</li>
 *   <li><strong>Désactivé:</strong> Fermé manuellement</li>
 * </ul>
 *
 * @author SophiaTech Eats Backend Team
 * @since 1.0
 */
@Getter
@Setter
public class TimeSlot implements Entity<UUID> {

    /** Identifiant unique du créneau */
    private final UUID id;

    /** Identifiant du restaurant associé */
    private final UUID restaurantId;

    /** Heure de début du créneau */
    private final LocalDateTime startTime;

    /** Heure de fin du créneau (startTime + 30 minutes) */
    private final LocalDateTime endTime;

    /** Capacité maximale de commandes pour ce créneau */
    private int maxCapacity;

    /** Nombre de réservations actuelles */
    private int reservedCount;
    private boolean available = true;

    /**
     * Constructeur privé utilisé par le Builder.
     */
    private TimeSlot(Builder builder) {
        this.id = builder.id;
        this.restaurantId = builder.restaurantId;
        this.startTime = builder.startTime;
        this.endTime = builder.endTime;
        this.maxCapacity = builder.maxCapacity;
        this.reservedCount = builder.reservedCount;
        this.available = builder.available;
        validate();
    }

    /**
     * Retourne un nouveau Builder pour créer un TimeSlot.
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Builder pour construire un TimeSlot de manière fluide.
     */
    public static class Builder {
        private UUID id;
        private UUID restaurantId;
        private LocalDateTime startTime;
        private LocalDateTime endTime;
        private int maxCapacity;
        private int reservedCount = 0;
        private boolean available = true;

        private Builder() {
        }

        /**
         * Définit l'ID du créneau (optionnel, un UUID aléatoire sera généré si non fourni).
         */
        public Builder id(UUID id) {
            this.id = id;
            return this;
        }

        /**
         * Définit l'ID du restaurant (obligatoire).
         */
        public Builder restaurantId(UUID restaurantId) {
            this.restaurantId = restaurantId;
            return this;
        }

        /**
         * Définit l'heure de début du créneau (obligatoire).
         */
        public Builder startTime(LocalDateTime startTime) {
            this.startTime = startTime;
            return this;
        }

        /**
         * Définit l'heure de début à partir d'une LocalTime et d'une date.
         */
        public Builder startTime(LocalTime time, LocalDateTime date) {
            if (time == null) {
                throw new ValidationException("L'heure de début ne peut pas être null");
            }
            if (date == null) {
                throw new ValidationException("La date ne peut pas être null");
            }
            this.startTime = date.with(time);
            return this;
        }

        /**
         * Définit l'heure de fin du créneau (obligatoire).
         */
        public Builder endTime(LocalDateTime endTime) {
            this.endTime = endTime;
            return this;
        }

        /**
         * Définit l'heure de fin à partir d'une LocalTime et d'une date.
         */
        public Builder endTime(LocalTime time, LocalDateTime date) {
            if (time == null) {
                throw new ValidationException("L'heure de fin ne peut pas être null");
            }
            if (date == null) {
                throw new ValidationException("La date ne peut pas être null");
            }
            this.endTime = date.with(time);
            return this;
        }

        /**
         * Définit un créneau de 30 minutes automatiquement à partir d'une heure de début.
         */
        public Builder thirtyMinuteSlot(LocalTime startTime, LocalDateTime date) {
            if (startTime == null) {
                throw new ValidationException("L'heure de début ne peut pas être null");
            }
            if (date == null) {
                throw new ValidationException("La date ne peut pas être null");
            }
            this.startTime = date.with(startTime);
            this.endTime = this.startTime.plusMinutes(30);
            return this;
        }

        /**
         * Définit la capacité maximale du créneau (obligatoire).
         */
        public Builder maxCapacity(int maxCapacity) {
            this.maxCapacity = maxCapacity;
            return this;
        }

        /**
         * Définit le nombre de places déjà réservées (optionnel, 0 par défaut).
         */
        public Builder reservedCount(int reservedCount) {
            this.reservedCount = reservedCount;
            return this;
        }

        /**
         * Définit si le créneau est disponible (optionnel, true par défaut).
         */
        public Builder available(boolean available) {
            this.available = available;
            return this;
        }

        /**
         * Construit le TimeSlot avec les paramètres définis.
         * @return Un nouveau TimeSlot
         * @throws ValidationException si les paramètres obligatoires sont manquants ou invalides
         */
        public TimeSlot build() {
            // Générer un ID si non fourni
            if (this.id == null) {
                this.id = UUID.randomUUID();
            }
            return new TimeSlot(this);
        }
    }

    /**
     * Constructeur principal pour créer un nouveau créneau.
     *
     * @param restaurantId l'identifiant du restaurant
     * @param startTime l'heure de début du créneau
     * @param endTime l'heure de fin du créneau
     * @param maxCapacity la capacité maximale de commandes
     * @throws ValidationException si les paramètres sont invalides
     */
    public TimeSlot(UUID restaurantId, LocalDateTime startTime, LocalDateTime endTime, int maxCapacity) {
        this(UUID.randomUUID(), restaurantId, startTime, endTime, maxCapacity);
    }

    /**
     * Constructeur de reconstruction pour les objets persistés.
     *
     * @param id l'identifiant existant du créneau
     * @param restaurantId l'identifiant du restaurant
     * @param startTime l'heure de début du créneau
     * @param endTime l'heure de fin du créneau
     * @param maxCapacity la capacité maximale de commandes
     * @throws ValidationException si les paramètres sont invalides
     */
    public TimeSlot(UUID id, UUID restaurantId, LocalDateTime startTime, LocalDateTime endTime, int maxCapacity) {
        this.id = id;
        this.restaurantId = restaurantId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.maxCapacity = maxCapacity;
        this.reservedCount = 0;
        validate();
    }

    /**
     * Constructeur utilitaire pour créer un créneau de 30 minutes à partir d'une heure.
     *
     * @param restaurantId l'identifiant du restaurant
     * @param startTime l'heure de début (LocalTime)
     * @param date la date du créneau
     * @param maxCapacity la capacité maximale de commandes
     * @throws ValidationException si les paramètres sont invalides
     */
    public TimeSlot(UUID restaurantId, LocalTime startTime, LocalDateTime date, int maxCapacity) {
        if (startTime == null) {
            throw new ValidationException("L'heure de début ne peut pas être null");
        }
        if (date == null) {
            throw new ValidationException("La date ne peut pas être null");
        }

        this.id = UUID.randomUUID();
        this.restaurantId = restaurantId;
        this.startTime = date.with(startTime);
        this.endTime = this.startTime.plusMinutes(30);
        this.maxCapacity = maxCapacity;
        this.reservedCount = 0;
        validate();
    }

    @Override
    public UUID getId() {
        return id;
    }


    /**
     * Vérifie si le créneau accepte encore des réservations.
     *
     * <p>Un créneau est disponible si:</p>
     * <ul>
     *   <li>Il est activé manuellement</li>
     *   <li>Il n'a pas atteint sa capacité maximale</li>
     *   <li>Il n'est pas dans le passé</li>
     * </ul>
     *
     * @return true si des réservations sont possibles
     */
    public boolean isAvailable() {
        return available && reservedCount < maxCapacity && !isPast();
    }

    /**
     * Tente de réserver une place dans le créneau.
     *
     * @return true si la réservation a réussi, false sinon
     */
    public boolean reserve() {
        if (!isAvailable()) {
            return false;
        }
        reservedCount++;
        return true;
    }

    /**
     * Réserve une place dans le créneau ou lève une exception.
     *
     * <p>Méthode recommandée pour les flux métier critiques où l'échec
     * de réservation doit être explicitement géré.</p>
     *
     * @throws ValidationException si la réservation est impossible
     */
    public void reserveOrThrow() {
        if (reservedCount >= maxCapacity) {
            throw new ValidationException("Impossible de réserver le créneau : capacité maximale atteinte (" + maxCapacity + ")");
        }
        if (!isAvailable()) {
            throw new ValidationException("Impossible de réserver le créneau : créneau expiré, désactivé ou complet");
        }
        reservedCount++;
    }

    /**
     * Libère une place précédemment réservée.
     *
     * @throws ValidationException si aucune réservation n'existe
     */
    public void release() {
        if (reservedCount <= 0) {
            throw new ValidationException("Impossible de libérer: aucune réservation existante");
        }
        reservedCount--;
    }

    /**
     * Vérifie si le créneau a atteint sa capacité maximale.
     *
     * @return true si plus aucune réservation n'est possible
     */
    public boolean isFull() {
        return reservedCount >= maxCapacity;
    }

    /**
     * Calcule le nombre de places encore disponibles.
     *
     * @return le nombre de réservations encore possibles
     */
    public int getAvailableSpots() {
        return Math.max(0, maxCapacity - reservedCount);
    }

    /**
     * Alias pour getAvailableSpots() - compatibilité avec l'ancien code.
     *
     * @return le nombre de places libres
     * @deprecated utiliser getAvailableSpots() pour plus de clarté
     */
    @Deprecated
    public int getCapacity() {
        return getAvailableSpots();
    }

    /**
     * Retourne le nombre de réservations actuelles.
     *
     * @return le nombre de places réservées
     */
    public int getCurrentCapacity() {
        return reservedCount;
    }

    // =================== GESTION DU TEMPS ===================

    /**
     * Vérifie si le créneau est dans le passé.
     *
     * <p>Un créneau est considéré comme passé si son heure de fin
     * est antérieure à l'instant présent.</p>
     *
     * @return true si le créneau est expiré
     */
    public boolean isPast() {
        return endTime.isBefore(LocalDateTime.now());
    }

    /**
     * Extrait l'heure de début sous forme LocalTime.
     *
     * @return l'heure de début sans la date
     */
    public LocalTime getStartTimeAsLocalTime() {
        return startTime.toLocalTime();
    }

    /**
     * Extrait l'heure de fin sous forme LocalTime.
     *
     * @return l'heure de fin sans la date
     */
    public LocalTime getEndTimeAsLocalTime() {
        return endTime.toLocalTime();
    }

    /**
     * Calcule l'heure de livraison selon les spécifications métier.
     *
     * <p>Selon les règles PO: "Temps de livraison = 15 minutes"
     * après la fin du créneau.</p>
     *
     * @return l'heure de livraison (fin + 15 minutes)
     */
    public LocalTime getDeliveryTime() {
        return endTime.toLocalTime().plusMinutes(15);
    }

    /**
     * Calcule l'instant complet de livraison (date + heure).
     *
     * @return l'instant de livraison complet
     */
    public LocalDateTime getDeliveryDateTime() {
        return endTime.plusMinutes(15);
    }

    // =================== CONFIGURATION ===================


    /**
     * Désactive temporairement le créneau.
     *
     * <p>Les créneaux désactivés n'acceptent plus de nouvelles réservations
     * mais conservent leurs réservations existantes.</p>
     */
    public void deactivate() {
        this.available = false;
    }

    /**
     * Réactive le créneau pour accepter des réservations.
     */
    public void activate() {
        this.available = true;
    }

    /**
     * Met à jour la capacité maximale du créneau.
     *
     * <p>La nouvelle capacité ne peut pas être inférieure au nombre
     * de réservations actuelles pour préserver l'intégrité des données.</p>
     *
     * @param newMaxCapacity la nouvelle capacité maximale
     * @throws ValidationException si la nouvelle capacité est invalide
     */
    public void setMaxCapacity(int newMaxCapacity) {
        if (newMaxCapacity <= 0) {
            throw new ValidationException("La capacité maximale doit être positive");
        }
        if (newMaxCapacity < reservedCount) {
            throw new ValidationException("La nouvelle capacité ne peut pas être inférieure aux réservations existantes");
        }
        this.maxCapacity = newMaxCapacity;
    }

    // =================== VALIDATION ET UTILITAIRES ===================

    @Override
    public void validate() {
        if (id == null) {
            throw new ValidationException("L'identifiant ne peut pas être null");
        }
        if (restaurantId == null) {
            throw new ValidationException("L'identifiant du restaurant ne peut pas être null");
        }
        if (startTime == null) {
            throw new ValidationException("L'heure de début ne peut pas être null");
        }
        if (endTime == null) {
            throw new ValidationException("L'heure de fin ne peut pas être null");
        }
        if (startTime.isAfter(endTime)) {
            throw new ValidationException("L'heure de début doit être antérieure à l'heure de fin");
        }
        if (maxCapacity <= 0) {
            throw new ValidationException("La capacité maximale doit être positive");
        }
        if (reservedCount < 0) {
            throw new ValidationException("Le nombre de réservations ne peut pas être négatif");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TimeSlot timeSlot = (TimeSlot) o;
        return Objects.equals(id, timeSlot.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("TimeSlot{id=%s, restaurant=%s, time=%s-%s, capacity=%d/%d, available=%b}",
                id, restaurantId,
                startTime.toLocalTime(), endTime.toLocalTime(),
                reservedCount, maxCapacity, available);
    }

}
