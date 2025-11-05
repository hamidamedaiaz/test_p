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

@Getter
@Setter
public class Cart implements Entity<UUID> {

    private final UUID id;
    private final UUID userId;
    private final List<CartItem> items;



    public Cart(UUID userId) {
        if (userId == null) {
            throw new ValidationException("alors l'identifiant utilisateur ne peut pas etre null");
        }

        this.id = UUID.randomUUID();
        this.userId = userId;
        this.items = new ArrayList<>();
    }


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

    public void removeDish(UUID dishId) {
        validate();
        items.removeIf(item -> item.getDishId().equals(dishId));
    }


    public BigDecimal calculateTotal() {
        return items.stream()
            .map(CartItem::getSubtotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }


    public int getTotalItems() {
        return items.stream()
            .mapToInt(CartItem::getQuantity)
            .sum();
    }


    public boolean isEmpty() {
        return items.isEmpty();
    }


    public void clear() {
        validate();
        items.clear();
    }


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

    private Optional<CartItem> findItemByDishId(UUID dishId) {
        return items.stream()
            .filter(item -> item.getDishId().equals(dishId))
            .findFirst();
    }

    private void validateAddition(Dish dish, int quantity) {
        if (dish == null) {
            throw new ValidationException("Le plat ne peut pas être null");
        }

        if (!dish.isAvailable()) {
            throw new ValidationException("Le plat n'est pas disponible: " + dish.getName());
        }

        validateQuantity(quantity);
    }

    private void validateQuantity(int quantity) {
        if (quantity <= 0) {
            throw new ValidationException("La quantité doit être positive");
        }
        if (quantity > 10) {
            throw new ValidationException("La quantité maximale par plat est de 10");
        }
    }
}
