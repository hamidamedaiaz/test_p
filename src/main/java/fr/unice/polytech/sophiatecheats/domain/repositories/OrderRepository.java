package fr.unice.polytech.sophiatecheats.domain.repositories;

import fr.unice.polytech.sophiatecheats.domain.entities.order.Order;
import fr.unice.polytech.sophiatecheats.domain.enums.OrderStatus;

import java.util.List;
import java.util.UUID;

/**
 * Repository interface for Order entities.
 */
public interface OrderRepository extends Repository<Order, String> {
    boolean existsActiveOrderByUserId(UUID userId);

    /**
     * Retourne toutes les commandes ayant le statut donné.
     */
    List<Order> findAllByStatus(OrderStatus status);
}