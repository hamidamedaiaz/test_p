package fr.unice.polytech.sophiatecheats.domain.services;

import fr.unice.polytech.sophiatecheats.domain.entities.cart.Cart;
import fr.unice.polytech.sophiatecheats.domain.entities.cart.CartItem;
import fr.unice.polytech.sophiatecheats.domain.entities.restaurant.Dish;
import fr.unice.polytech.sophiatecheats.domain.entities.user.User;
import fr.unice.polytech.sophiatecheats.domain.repositories.CartRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class CartService {

    private final CartRepository repository;

    public CartService(CartRepository repository) {
        this.repository = repository;
    }

    public Cart getCart(User user) {
        return repository.findActiveCartByUserId(user.getId())
                .orElseGet(() -> {
                    Cart cart = new Cart(user.getId());
                    repository.save(cart);
                    return cart;
                });
    }

    public void addToCart(User user, Dish dish, int quantity) {
        Cart cart = getCart(user);
        cart.addDish(dish, quantity);
        repository.save(cart);
    }

    public void removeFromCart(User user, UUID dishId) {
        Cart cart = getCart(user);
        cart.removeDish(dishId);
        repository.save(cart);
    }

    public void clearCart(User user) {
        Cart cart = getCart(user);
        cart.clear();
        repository.save(cart);
    }

    public List<CartItem> getItems(User user) {
        Cart cart = getCart(user);
        return cart.getItems();
    }

    public boolean isEmpty(User user) {
        Cart cart = getCart(user);
        return cart.isEmpty();
    }

    public BigDecimal calculateTotal(User user) {
        Cart cart = getCart(user);
        return cart.calculateTotal();
    }
}
