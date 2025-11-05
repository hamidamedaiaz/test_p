package fr.unice.polytech.sophiatecheats.infrastructure.repositories.memory;

import fr.unice.polytech.sophiatecheats.domain.entities.order.Order;
import fr.unice.polytech.sophiatecheats.domain.repositories.OrderRepository;

/**
 * In-memory implementation of OrderRepository for MVP.
 */
public class InMemoryOrderRepository extends InMemoryRepository<Order, String> implements OrderRepository {

    @Override
    protected String extractId(Order order) {
        return order.getOrderId();
    }
}