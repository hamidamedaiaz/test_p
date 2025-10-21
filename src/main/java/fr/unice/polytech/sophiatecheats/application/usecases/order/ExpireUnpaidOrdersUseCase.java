package fr.unice.polytech.sophiatecheats.application.usecases.order;

import fr.unice.polytech.sophiatecheats.domain.entities.order.Order;
import fr.unice.polytech.sophiatecheats.domain.enums.OrderStatus;
import fr.unice.polytech.sophiatecheats.domain.repositories.OrderRepository;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Use case batch pour expirer les commandes non payées après 5 minutes de réservation du créneau.
 * Libère automatiquement le créneau de livraison.
 */
public class ExpireUnpaidOrdersUseCase implements Runnable {
    private final OrderRepository orderRepository;
    private final Duration paymentTimeout;

    public ExpireUnpaidOrdersUseCase(OrderRepository orderRepository, Duration paymentTimeout) {
        this.orderRepository = orderRepository;
        this.paymentTimeout = paymentTimeout;
    }

    @Override
    public void run() {
        // Récupérer toutes les commandes PENDING avec un créneau réservé
        List<Order> pendingOrders = orderRepository.findAllByStatus(OrderStatus.PENDING);
        LocalDateTime now = LocalDateTime.now();
        for (Order order : pendingOrders) {
            if (order.hasDeliverySlot() && order.getDeliverySlotReservedAt() != null) {
                Duration sinceReservation = Duration.between(order.getDeliverySlotReservedAt(), now);
                if (sinceReservation.compareTo(paymentTimeout) > 0) {
                    order.expire();
                    orderRepository.save(order);
                }
            }
        }
    }
}

