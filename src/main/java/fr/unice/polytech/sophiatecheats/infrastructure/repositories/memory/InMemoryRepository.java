package fr.unice.polytech.sophiatecheats.infrastructure.repositories.memory;

import fr.unice.polytech.sophiatecheats.domain.repositories.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Implémentation abstraite d'un repository en mémoire.
 * Utilise une Map thread-safe pour le stockage.
 *
 * @param <T> le type d'entité
 * @param <ID> le type d'identifiant
 */
public abstract class InMemoryRepository<T, ID> implements Repository<T, ID> {

  protected final Map<ID, T> storage = new ConcurrentHashMap<>();

  /**
   * Extrait l'ID d'une entité.
   * Méthode abstraite à implémenter par les repositories concrets.
   */
  protected abstract ID extractId(T entity);

  @Override
  public T save(T entity) {
    if (entity == null) {
      throw new IllegalArgumentException("Entity cannot be null");
    }

    ID id = extractId(entity);
    if (id == null) {
      throw new IllegalArgumentException("Entity ID cannot be null");
    }

    storage.put(id, entity);
    return entity;
  }

  @Override
  public Optional<T> findById(ID id) {
    if (id == null) {
      return Optional.empty();
    }
    return Optional.ofNullable(storage.get(id));
  }

  @Override
  public List<T> findAll() {
    return new ArrayList<>(storage.values());
  }

  @Override
  public boolean deleteById(ID id) {
    if (id == null) {
      return false;
    }
    return storage.remove(id) != null;
  }

  @Override
  public boolean existsById(ID id) {
    if (id == null) {
      return false;
    }
    return storage.containsKey(id);
  }

  /**
   * Retourne le nombre d'entités stockées.
   */
  public int size() {
    return storage.size();
  }

  /**
   * Vide le repository.
   */
  public void clear() {
    storage.clear();
  }
}
