package fr.unice.polytech.sophiatecheats.domain.entities.cart;

import fr.unice.polytech.sophiatecheats.domain.entities.restaurant.Dish;
import fr.unice.polytech.sophiatecheats.domain.exceptions.ValidationException;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
public class CartItem {

    private final Dish dish;
    private final UUID dishId;
    private final String dishName;
    private final String dishDescription;
    private final BigDecimal unitPrice;
    private int quantity;

    public CartItem(Dish dish, int quantity) {
        if (dish == null) {
            throw new ValidationException("le plat ne peut pas être null");
        }

        validateQuantity(quantity);

        this.dish = dish;
        this.dishId = dish.getId();
        this.dishName = dish.getName();
        this.dishDescription = dish.getDescription();
        this.unitPrice = dish.getPrice();
        this.quantity = quantity;
    }

    public void updateQuantity(int newQuantity) {
        validateQuantity(newQuantity);
        this.quantity = newQuantity;
    }

    public BigDecimal getSubtotal() {
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }


    private void validateQuantity(int quantity) {
        if (quantity <= 0) {
            throw new ValidationException("erreur ");
        }
    }

    @Override
    public String toString() {
        return String.format("CartItem{dish='%s', quantity=%d, subtotal=%.2f€}",
                           dishName, quantity, getSubtotal());
    }
}
