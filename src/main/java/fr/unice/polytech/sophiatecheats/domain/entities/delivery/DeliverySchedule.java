package fr.unice.polytech.sophiatecheats.domain.entities.delivery;

import fr.unice.polytech.sophiatecheats.domain.entities.restaurant.TimeSlot;
import fr.unice.polytech.sophiatecheats.domain.entities.restaurant.Schedule;
import fr.unice.polytech.sophiatecheats.domain.exceptions.SlotNotFoundException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

/**
 * Représente l'ensemble des créneaux de livraison disponibles pour un restaurant donné.
 * Gère la génération, la recherche et la réservation des créneaux.
 */
public class DeliverySchedule {
    private final UUID restaurantId;
    private final Map<LocalDate, List<TimeSlot>> slotsByDate = new HashMap<>();

    public DeliverySchedule(UUID restaurantId) {
        this.restaurantId = restaurantId;
    }

    /**
     * Génère des créneaux de 30 min pour un jour donné en respectant les heures d'ouverture du restaurant.
     * @param date La date pour laquelle générer les créneaux
     * @param restaurantSchedule Les heures d'ouverture du restaurant
     * @param maxCapacityPerSlot La capacité maximale par créneau
     */
    public void generateDailySlots(LocalDate date, Schedule restaurantSchedule, int maxCapacityPerSlot) {
        if (restaurantSchedule == null) {
            throw new IllegalArgumentException("Le planning du restaurant ne peut pas être null");
        }

        List<TimeSlot> slots = new ArrayList<>();
        LocalTime openingTime = restaurantSchedule.getOpeningTime();
        LocalTime closingTime = restaurantSchedule.getClosingTime();

        // Commence à l'heure d'ouverture, se termine à l'heure de fermeture
        LocalDateTime current = date.atTime(openingTime);
        LocalDateTime endTime = date.atTime(closingTime);

        while (current.isBefore(endTime)) {
            LocalDateTime slotEnd = current.plusMinutes(30);

            // Ne crée le créneau que s'il se termine avant la fermeture
            if (!slotEnd.isAfter(endTime)) {
                slots.add(new TimeSlot(restaurantId, current, slotEnd, maxCapacityPerSlot));
            }
            current = slotEnd;
        }

        slotsByDate.put(date, slots);
    }

    /**
     * Version legacy - génère des créneaux entre des heures spécifiques
     * @deprecated Utilisez generateDailySlots(LocalDate, Schedule, int) à la place
     */
    @Deprecated
    public void generateDailySlots(LocalDate date, LocalTime start, LocalTime end, int maxCapacityPerSlot) {
        // Crée un Schedule temporaire pour la compatibilité
        Schedule tempSchedule = new Schedule(start, end);
        generateDailySlots(date, tempSchedule, maxCapacityPerSlot);
    }

    /**
     * Génère automatiquement les créneaux pour une semaine en respectant le planning du restaurant.
     * @param startDate Date de début de la semaine
     * @param restaurantSchedule Planning d'ouverture du restaurant
     * @param maxCapacityPerSlot Capacité maximale par créneau
     */
    public void generateWeeklySlots(LocalDate startDate, Schedule restaurantSchedule, int maxCapacityPerSlot) {
        for (int i = 0; i < 7; i++) {
            LocalDate date = startDate.plusDays(i);
            generateDailySlots(date, restaurantSchedule, maxCapacityPerSlot);
        }
    }

    /**
     * Obtient les créneaux disponibles (non pleins et dans le futur) pour une date.
     * @param date La date recherchée
     * @return Liste des créneaux disponibles
     */
    public List<TimeSlot> getAvailableSlotsForDate(LocalDate date) {
        return getSlotsForDate(date).stream()
                .filter(TimeSlot::isAvailable)
                .toList();
    }

    public List<TimeSlot> getSlotsForDate(LocalDate date) {
        return slotsByDate.getOrDefault(date, Collections.emptyList());
    }

    /**
     * Compte le nombre total de créneaux disponibles pour une date.
     * @param date La date recherchée
     * @return Nombre de créneaux disponibles
     */
    public int getAvailableSlotCount(LocalDate date) {
        return getAvailableSlotsForDate(date).size();
    }

    public Optional<TimeSlot> findSlotById(UUID slotId) {
        return slotsByDate.values().stream()
                .flatMap(Collection::stream)
                .filter(slot -> slot.getId().equals(slotId))
                .findFirst();
    }

    public void reserveSlot(UUID slotId) {
        Optional<TimeSlot> slotOpt = findSlotById(slotId);
        if (slotOpt.isEmpty()) {
            throw new SlotNotFoundException("The reservation of " + slotId + " is not possible.");
        }
        TimeSlot slot = slotOpt.get();

        if (!slot.isAvailable()) {
            throw new SlotNotFoundException("Le créneau " + slotId + " n'est plus disponible.");
        }

        slot.reserveOrThrow();
    }

    public void releaseSlot(UUID slotId) {
        findSlotById(slotId).ifPresent(TimeSlot::release);
    }

    /**
     * Supprime tous les créneaux passés pour libérer la mémoire.
     */
    public void cleanupPastSlots() {
        LocalDateTime now = LocalDateTime.now();
        slotsByDate.entrySet().removeIf(entry ->
            entry.getKey().isBefore(now.toLocalDate()) ||
            (entry.getKey().equals(now.toLocalDate()) &&
             entry.getValue().stream().allMatch(slot -> slot.getEndTime().isBefore(now)))
        );
    }

    public UUID getRestaurantId() {
        return restaurantId;
    }
}
