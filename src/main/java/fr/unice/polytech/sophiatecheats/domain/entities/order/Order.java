package fr.unice.polytech.sophiatecheats.domain.entities.order;

import fr.unice.polytech.sophiatecheats.domain.entities.restaurant.Restaurant;
import fr.unice.polytech.sophiatecheats.domain.entities.user.User;
import fr.unice.polytech.sophiatecheats.domain.enums.OrderStatus;
import fr.unice.polytech.sophiatecheats.domain.enums.PaymentMethod;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
@Getter
@Setter
public class Order {
    private String orderId;
    private User user;
    private Restaurant restaurant;
    private List<OrderItem> orderItems;
    private OrderStatus status;
    private BigDecimal totalAmount;
    private LocalDateTime orderDateTime;
    private LocalDateTime deliveryTime;
    private PaymentMethod paymentMethod;
    private UUID deliverySlotId; // Link to the reserved delivery slot

    public Order(User user, Restaurant restaurant, List<OrderItem> orderItems,
                    PaymentMethod paymentMethod) {
        this.orderId = UUID.randomUUID().toString();
        this.user=user;
        this.restaurant=restaurant;
        this.orderItems=orderItems;
        this.status=OrderStatus.PENDING;
        this.totalAmount= calculateTotalAmount();
        this.orderDateTime=LocalDateTime.now();
        this.paymentMethod=paymentMethod;
        this.deliverySlotId = null; // Will be set when slot is selected
    }


   public BigDecimal calculateTotalAmount() {
                   return orderItems.stream()
                           .map(OrderItem::getTotalPrice)
                           .reduce(BigDecimal.ZERO, BigDecimal::add);
       }

    /**
     * Confirme la commande en changeant son statut vers CONFIRMED.
     * Calcule également le temps de livraison estimé (15 minutes après confirmation).
     * @throws IllegalStateException si la commande ne peut pas être confirmée
     */
    public void confirm() {
        if (status == OrderStatus.CONFIRMED) {
            throw new IllegalStateException("La commande est déjà confirmée");
        }
        if (status == OrderStatus.EXPIRED) {
            throw new IllegalStateException("La commande a expiré et ne peut pas être confirmée");
        }

        this.status = OrderStatus.CONFIRMED;
        this.deliveryTime = LocalDateTime.now().plusMinutes(15);
    }

    /**
     * Vérifie si la commande peut être confirmée.
     * @return true si la commande peut être confirmée
     */
    public boolean canBeConfirmed() {
        return status == OrderStatus.PENDING || status == OrderStatus.PAID;
    }

    /**
     * Marque la commande comme payée.
     */
    public void markAsPaid() {
        if (status == OrderStatus.EXPIRED) {
            throw new IllegalStateException("La commande a expiré et ne peut pas être payée");
        }
        this.status = OrderStatus.PAID;
    }

    /**
     * Assigns a delivery slot to this order and reserves it.
     * This should be called after order creation but before payment processing.
     */
    public void assignDeliverySlot(UUID slotId, LocalDateTime slotStartTime) {
        if (this.deliverySlotId != null) {
            throw new IllegalStateException("Order already has a delivery slot assigned");
        }
        if (slotId == null) {
            throw new IllegalArgumentException("Slot ID cannot be null");
        }
        this.deliverySlotId = slotId;
        this.deliveryTime = slotStartTime;
    }

    /**
     * Releases the delivery slot (e.g., on payment timeout or cancellation).
     */
    public void releaseDeliverySlot() {
        this.deliverySlotId = null;
        this.deliveryTime = null;
    }

    /**
     * Vérifie si la commande a un créneau de livraison assigné.
     */
    public boolean hasDeliverySlot() {
        return deliverySlotId != null;
    }

}
