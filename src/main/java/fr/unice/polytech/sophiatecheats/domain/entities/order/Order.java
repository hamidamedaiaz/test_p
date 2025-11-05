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
    private boolean isPaid;  // ✅ NOUVEAU: Indique explicitement si la commande est payée

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
        this.isPaid = false;
    }

    /**
     * Marque la commande comme payée.
     * Cette méthode doit être appelée APRÈS que le paiement soit vérifié et traité.
     */
    public void markAsPaid() {
        this.isPaid = true;
    }

   public BigDecimal calculateTotalAmount() {
                   return orderItems.stream()
                           .map(OrderItem::getTotalPrice)
                           .reduce(BigDecimal.ZERO, BigDecimal::add);
       }


   }
