package fr.unice.polytech.sophiatecheats.domain.repositories;

import fr.unice.polytech.sophiatecheats.domain.entities.cart.Cart;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository pour la gestion des paniers de commande.
 *
 * <p>Ce repository gère la persistance des paniers temporaires des utilisateurs.
 * Un utilisateur ne peut avoir qu'un seul panier actif à la fois.</p>
 *
 * <h3>Responsabilités:</h3>
 * <ul>
 *     <li>Récupération du panier actif d'un utilisateur</li>
 *     <li>Sauvegarde des modifications du panier</li>
 *     <li>Nettoyage des paniers expirés</li>
 * </ul>
 *
 * @author SophiaTech Eats Backend Team
 * @since 1.0
 */
public interface CartRepository extends Repository<Cart, UUID> {

    /**
     * Trouve le panier actif d'un utilisateur.
     *
     * <p>Un utilisateur ne peut avoir qu'un seul panier actif à la fois.
     * Si aucun panier n'existe, retourne Optional.empty().</p>
     *
     * @param userId l'identifiant de l'utilisateur
     * @return le panier actif de l'utilisateur ou Optional.empty()
     */
    Optional<Cart> findActiveCartByUserId(UUID userId);

    /**
     * Supprime tous les paniers expirés.
     *
     * <p>Les paniers sont considérés comme expirés s'ils n'ont pas été
     * modifiés depuis un certain délai (ex: 24 heures).</p>
     *
     * @return le nombre de paniers supprimés
     */
    int deleteExpiredCarts();

    /**
     * Vérifie si un utilisateur a un panier actif.
     *
     * @param userId l'identifiant de l'utilisateur
     * @return true si l'utilisateur a un panier actif
     */
    boolean hasActiveCart(UUID userId);
}
