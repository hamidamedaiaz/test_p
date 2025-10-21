package fr.unice.polytech.sophiatecheats.domain.repositories;

import java.util.List;
import java.util.Optional;

/**
 * Interface générique pour tous les repositories du domaine.
 * Définit les opérations CRUD de base selon les principes Clean Architecture.
 *
 * @param <T> le type d'entité
 * @param <ID> le type d'identifiant
 */
public interface Repository<T, ID> {

  /**
   * Sauvegarde une entité.
   * @param entity l'entité à sauvegarder
   * @return l'entité sauvegardée
   */
  T save(T entity);

  /**
   * Trouve une entité par son identifiant.
   * @param id l'identifiant de l'entité
   * @return Optional contenant l'entité si trouvée
   */
  Optional<T> findById(ID id);

  /**
   * Retourne toutes les entités.
   * @return la liste de toutes les entités
   */
  List<T> findAll();

  /**
   * Supprime une entité par son identifiant.
   * @param id l'identifiant de l'entité à supprimer
   * @return true si l'entité a été supprimée, false sinon
   */
  boolean deleteById(ID id);

  /**
   * Vérifie si une entité existe.
   * @param id l'identifiant à vérifier
   * @return true si l'entité existe, false sinon
   */
  boolean existsById(ID id);
}
