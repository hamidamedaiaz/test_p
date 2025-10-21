package fr.unice.polytech.sophiatecheats.domain.repositories;

import fr.unice.polytech.sophiatecheats.domain.entities.restaurant.TimeSlot;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Repository pour la gestion des créneaux de livraison
 */
public interface TimeSlotRepository extends Repository<TimeSlot, UUID> {

    /**
     * @param date La date du jour pour laquelle on cherche les créneaux disponibles
     * @return Liste des TimeSlot encore disponibles pour la date donnée
     */
    List<TimeSlot> findAvailableSlots(LocalDate date);

    /**
     * @param slot Le créneau à sauvegarder
     * @return Le créneau sauvegardé (potentiellement enrichi d’un ID)
     */
    @Override
    TimeSlot save(TimeSlot slot);

    /**
     * Met à jour un créneau existant.
     * @param slot Le créneau à mettre à jour.
     */
    void update(TimeSlot slot);
}
