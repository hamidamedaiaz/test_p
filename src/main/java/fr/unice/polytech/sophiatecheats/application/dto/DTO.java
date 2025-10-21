package fr.unice.polytech.sophiatecheats.application.dto;

/**
 * Interface de base pour tous les Data Transfer Objects.
 * Les DTOs servent à transférer des données entre les couches
 * sans exposer directement les entités du domaine.
 */
public interface DTO {

  /**
   * Valide les données du DTO.
   * @return true si les données sont valides, false sinon
   */
  default boolean isValid() {
    return true;
  }
}
