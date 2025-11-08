package fr.unice.polytech.sophiatecheats.domain.entities.cart;

import fr.unice.polytech.sophiatecheats.domain.entities.Entity;
import fr.unice.polytech.sophiatecheats.domain.entities.restaurant.Dish;
import fr.unice.polytech.sophiatecheats.domain.exceptions.ValidationException;
import fr.unice.polytech.sophiatecheats.domain.exceptions.CannotMixRestaurantsException;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Représente un panier d'achat pour un utilisateur dans le système SophiaTech Eats.
 *
 * <h3>Règles métier appliquées:</h3>
 * <ul>
 *   <li>Maximum 10 articles par type de plat</li>
 *   <li>Seuls les plats disponibles peuvent être ajoutés</li>
 *   <li>Tous les plats doivent provenir du même restaurant</li>
 * </ul>
 *
 * @author Saad
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

    /** Date et heure de création du panier */
    private final LocalDateTime createdAt;

    /**
     * Identifiant du restaurant dont proviennent les plats du panier.
     * Null si le panier est vide.
     */
    private UUID restaurantId;

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
        this.restaurantId = null; // Aucun restaurant au départ
        this.createdAt = LocalDateTime.now();
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
     *   <li>Vérification que le plat provient du bon restaurant</li>
     * </ul></p>
     *
     * @param dish le plat à ajouter
     * @param quantity la quantité à ajouter (doit être positive et ≤ 10)
     * @param dishRestaurantId l'identifiant du restaurant du plat
     * @throws ValidationException si le plat est null, indisponible ou si la quantité est invalide
     * @throws CannotMixRestaurantsException si le plat provient d'un restaurant différent
     */
    public void addDish(Dish dish, int quantity, UUID dishRestaurantId) {
        validate();
        validateAddition(dish, quantity);
        validateRestaurant(dishRestaurantId);

        Optional<CartItem> existingItem = findItemByDishId(dish.getId());

        if (existingItem.isPresent()) {
            int newQuantity = existingItem.get().getQuantity() + quantity;
            validateQuantity(newQuantity);
            existingItem.get().updateQuantity(newQuantity);
        } else {
            items.add(new CartItem(dish, quantity));

            // Si c'est le premier plat, définir le restaurant du panier
            if (restaurantId == null) {
                restaurantId = dishRestaurantId;
            }
        }
    }

    /**
     * Version de compatibilité pour le code existant.
     * Utilise addDish(dish, quantity, null) qui lèvera une exception si le panier n'est pas vide.
     *
     * @deprecated Utilisez addDish(dish, quantity, restaurantId) à la place
     */
    @Deprecated
    public void addDish(Dish dish, int quantity) {
        // If the Dish has a restaurantId set (populated by Restaurant.addDish), delegate
        // to the strict method to enforce the "single restaurant" rule.
        if (dish != null && dish.getRestaurantId() != null) {
            addDish(dish, quantity, dish.getRestaurantId());
            return;
        }

        // Fallback: apply local validations and add without setting restaurantId.
        validate();
        validateAddition(dish, quantity);

        Optional<CartItem> existingItem = findItemByDishId(dish.getId());

        if (existingItem.isPresent()) {
            int newQuantity = existingItem.get().getQuantity() + quantity;
            validateQuantity(newQuantity);
            existingItem.get().updateQuantity(newQuantity);
        } else {
            items.add(new CartItem(dish, quantity));
            // Ne pas modifier restaurantId ici : on ne dispose pas de l'UUID du
            // restaurant dans la surcharge dépréciée. L'appelant devrait utiliser
            // addDish(dish, quantity, restaurantId) lorsqu'il connaît l'ID.
        }
    }

    /**
     * Met à jour la quantité d'un plat spécifique dans le panier.
     *
     * @param dishId l'identifiant du plat à modifier
     * @param quantity la nouvelle quantité (0 pour supprimer)
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

        // Si le panier devient vide, réinitialiser le restaurant
        if (items.isEmpty()) {
            restaurantId = null;
        }
    }

    /**
     * Vide complètement le panier en supprimant tous les articles.
     */
    public void clear() {
        validate();
        items.clear();
        restaurantId = null; // Réinitialiser le restaurant
    }

    /**
     * Calcule le montant total de tous les articles du panier.
     */
    public BigDecimal calculateTotal() {
        return items.stream()
                .map(CartItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Calcule le nombre total d'articles dans le panier.
     */
    public int getTotalItems() {
        return items.stream()
                .mapToInt(CartItem::getQuantity)
                .sum();
    }

    /**
     * Vérifie si le panier est vide.
     */
    public boolean isEmpty() {
        return items.isEmpty();
    }

    /**
     * Retourne une copie immutable de la liste des articles du panier.
     */
    public List<CartItem> getItems() {
        return List.copyOf(items);
    }

    /**
     * Vérifie si le panier appartient à un restaurant spécifique.
     */
    public boolean belongsToRestaurant(UUID restaurantId) {
        return this.restaurantId != null && this.restaurantId.equals(restaurantId);
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
     */
    private Optional<CartItem> findItemByDishId(UUID dishId) {
        return items.stream()
                .filter(item -> item.getDishId().equals(dishId))
                .findFirst();
    }

    /**
     * Valide les conditions pour ajouter un plat au panier.
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
     */
    private void validateQuantity(int quantity) {
        if (quantity <= 0) {
            throw new ValidationException("La quantité doit être positive");
        }
        if (quantity > 10) {
            throw new ValidationException("La quantité maximale par plat est de 10");
        }
    }

    /**
     * Valide que le plat provient du bon restaurant.
     *
     * @param dishRestaurantId l'identifiant du restaurant du plat à ajouter
     * @throws CannotMixRestaurantsException si le plat provient d'un restaurant différent
     */
    private void validateRestaurant(UUID dishRestaurantId) {
        if (dishRestaurantId == null) {
            throw new ValidationException("L'identifiant du restaurant ne peut pas être null");
        }

        // Si le panier a déjà un restaurant et que ce n'est pas le même
        if (restaurantId != null && !restaurantId.equals(dishRestaurantId)) {
            throw new CannotMixRestaurantsException();
        }
    }
}
