package fr.unice.polytech.sophiatecheats.domain.entities;

/**
 * Interface de base pour toutes les entités du domaine.
 * Définit le contrat minimum qu'une entité doit respecter.
 */
public interface Entity<ID> {

  /**
   * Retourne l'identifiant unique de l'entité.
   * @return l'identifiant de l'entité
   */
  ID getId();

  /**
   * Vérifie si l'entité est valide selon les règles métier.
   * @throws fr.unice.polytech.sophiatecheats.domain.exceptions.DomainException si l'entité n'est pas valide
   */
  void validate();
}
