package fr.unice.polytech.sophiatecheats.domain.repositories;

import fr.unice.polytech.sophiatecheats.domain.entities.order.Order;

import java.util.UUID;

/**
 * Repository interface for Order entities.
 */
public interface OrderRepository extends Repository<Order, String> {
    boolean existsActiveOrderByUserId(UUID userId);

}