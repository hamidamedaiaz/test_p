package fr.unice.polytech.sophiatecheats.domain.entities.cart;

import fr.unice.polytech.sophiatecheats.domain.entities.Entity;
import fr.unice.polytech.sophiatecheats.domain.entities.restaurant.Dish;
import fr.unice.polytech.sophiatecheats.domain.exceptions.ValidationException;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Représente un panier d'achat pour un utilisateur dans le système SophiaTech Eats.
 *
 * <p>Un panier permet à un utilisateur de collecter des plats avant de passer commande.
 * Il gère automatiquement les quantités, les totaux et applique les règles métier
 * telles que les limites de quantité par plat.</p>
 *
 * <h3>Règles métier appliquées:</h3>
 * <ul>
 *   <li>Maximum 10 articles par type de plat</li>
 *   <li>Seuls les plats disponibles peuvent être ajoutés</li>
 *   <li>Mise à jour automatique des quantités pour les plats existants</li>
 *   <li>Suppression automatique des articles à quantité zéro</li>
 * </ul>
 *
 * @author SophiaTech Eats Backend Team
 * @since 1.0
 */
@Getter
@Setter
public class Cart implements Entity<UUID> {

    /** Identifiant unique du panier */
    private final UUID id;

    /** Identifiant de l'utilisateur propriétaire du panier */
    private final UUID userId;

    /** Liste des articles dans le panier */
    private final List<CartItem> items;

    /**
     * Crée un nouveau panier pour l'utilisateur spécifié.
     *
     * @param userId l'identifiant de l'utilisateur propriétaire
     * @throws ValidationException si l'identifiant utilisateur est null
     */
    public Cart(UUID userId) {
        if (userId == null) {
            throw new ValidationException("L'identifiant utilisateur ne peut pas être null");
        }

        this.id = UUID.randomUUID();
        this.userId = userId;
        this.items = new ArrayList<>();
    }

    @Override
    public UUID getId() {
        return id;
    }

    /**
     * Ajoute un plat au panier ou met à jour sa quantité s'il existe déjà.
     *
     * <p>Cette méthode applique automatiquement les règles métier:
     * <ul>
     *   <li>Vérification de la disponibilité du plat</li>
     *   <li>Validation des quantités (1-10 par plat)</li>
     *   <li>Fusion automatique avec les articles existants</li>
     * </ul></p>
     *
     * @param dish le plat à ajouter
     * @param quantity la quantité à ajouter (doit être positive et ≤ 10)
     * @throws ValidationException si le plat est null, indisponible ou si la quantité est invalide
     */
    public void addDish(Dish dish, int quantity) {
        validate();
        validateAddition(dish, quantity);

        Optional<CartItem> existingItem = findItemByDishId(dish.getId());

        if (existingItem.isPresent()) {
            int newQuantity = existingItem.get().getQuantity() + quantity;
            validateQuantity(newQuantity);
            existingItem.get().updateQuantity(newQuantity);
        } else {
            items.add(new CartItem(dish, quantity));
        }
    }

    /**
     * Met à jour la quantité d'un plat spécifique dans le panier.
     *
     * <p>Si la quantité est définie à 0 ou moins, l'article est automatiquement
     * supprimé du panier.</p>
     *
     * @param dishId l'identifiant du plat à modifier
     * @param quantity la nouvelle quantité (0 pour supprimer)
     * @throws ValidationException si le plat n'est pas dans le panier ou si la quantité est invalide
     */
    public void updateQuantity(UUID dishId, int quantity) {
        validate();

        if (quantity <= 0) {
            removeDish(dishId);
            return;
        }

        validateQuantity(quantity);

        CartItem item = findItemByDishId(dishId)
            .orElseThrow(() -> new ValidationException("Plat non trouvé dans le panier: " + dishId));

        item.updateQuantity(quantity);
    }

    /**
     * Supprime un plat spécifique du panier.
     *
     * @param dishId l'identifiant du plat à supprimer
     */
    public void removeDish(UUID dishId) {
        validate();
        items.removeIf(item -> item.getDishId().equals(dishId));
    }

    /**
     * Calcule le montant total de tous les articles du panier.
     *
     * @return le montant total en BigDecimal
     */
    public BigDecimal calculateTotal() {
        return items.stream()
            .map(CartItem::getSubtotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Calcule le nombre total d'articles dans le panier.
     *
     * @return la somme des quantités de tous les articles
     */
    public int getTotalItems() {
        return items.stream()
            .mapToInt(CartItem::getQuantity)
            .sum();
    }

    /**
     * Vérifie si le panier est vide.
     *
     * @return true si le panier ne contient aucun article
     */
    public boolean isEmpty() {
        return items.isEmpty();
    }

    /**
     * Vide complètement le panier en supprimant tous les articles.
     */
    public void clear() {
        validate();
        items.clear();
    }

    /**
     * Retourne une copie immutable de la liste des articles du panier.
     *
     * @return une liste non modifiable des articles
     */
    public List<CartItem> getItems() {
        return List.copyOf(items);
    }

    @Override
    public void validate() {
        if (userId == null) {
            throw new ValidationException("L'identifiant utilisateur ne peut pas être null");
        }
        if (items == null) {
            throw new ValidationException("La liste des articles ne peut pas être null");
        }
        if (id == null) {
            throw new ValidationException("L'identifiant du panier ne peut pas être null");
        }
    }

    /**
     * Recherche un article dans le panier par l'identifiant du plat.
     *
     * @param dishId l'identifiant du plat recherché
     * @return un Optional contenant l'article s'il existe
     */
    private Optional<CartItem> findItemByDishId(UUID dishId) {
        return items.stream()
            .filter(item -> item.getDishId().equals(dishId))
            .findFirst();
    }

    /**
     * Valide les conditions pour ajouter un plat au panier.
     *
     * @param dish le plat à valider
     * @param quantity la quantité à valider
     * @throws ValidationException si les conditions ne sont pas respectées
     */
    private void validateAddition(Dish dish, int quantity) {
        if (dish == null) {
            throw new ValidationException("Le plat ne peut pas être null");
        }

        if (!dish.isAvailable()) {
            throw new ValidationException("Le plat n'est pas disponible: " + dish.getName());
        }

        validateQuantity(quantity);
    }

    /**
     * Valide qu'une quantité respecte les règles métier.
     *
     * @param quantity la quantité à valider
     * @throws ValidationException si la quantité est <= 0 ou > 10
     */
    private void validateQuantity(int quantity) {
        if (quantity <= 0) {
            throw new ValidationException("La quantité doit être positive");
        }
        if (quantity > 10) {
            throw new ValidationException("La quantité maximale par plat est de 10");
        }
    }
}
