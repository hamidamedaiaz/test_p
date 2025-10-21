package fr.unice.polytech.sophiatecheats.application.usecases;

/**
 * Interface de base pour tous les cas d'utilisation de l'application.
 * Suit le principe de responsabilité unique : un use case = une action métier.
 *
 * @param <Input> le type de données d'entrée
 * @param <Output> le type de données de sortie
 */
public interface UseCase<Input, Output> {

  /**
   * Exécute le cas d'utilisation.
   * @param input les données d'entrée
   * @return le résultat de l'exécution
   */
  Output execute(Input input);
}
