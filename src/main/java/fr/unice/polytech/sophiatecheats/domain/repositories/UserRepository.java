package fr.unice.polytech.sophiatecheats.domain.repositories;

import fr.unice.polytech.sophiatecheats.domain.entities.user.User;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository pour la gestion des utilisateurs.
 * Interface simple pour les opérations CRUD de base sur les utilisateurs.
 */
public interface UserRepository extends Repository<User, UUID> {

    /**
     * Trouve un utilisateur par son email.
     * @param email l'email de l'utilisateur
     * @return l'utilisateur s'il existe
     */
    Optional<User> findByEmail(String email);
}
