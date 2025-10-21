package fr.unice.polytech.sophiatecheats.infrastructure.repositories.memory;

import fr.unice.polytech.sophiatecheats.domain.entities.order.Order;
import fr.unice.polytech.sophiatecheats.domain.enums.OrderStatus;
import fr.unice.polytech.sophiatecheats.domain.repositories.OrderRepository;

import java.util.UUID;

/**
 * In-memory implementation of OrderRepository for MVP.
 */
public class InMemoryOrderRepository extends InMemoryRepository<Order, String> implements OrderRepository {

    @Override
    protected String extractId(Order order) {
        return order.getOrderId();
    }

    @Override
    public boolean existsActiveOrderByUserId(UUID userId) {
        for (Order order : storage.values()) {
            if (order.getUser().getId().equals(userId) &&
                (order.getStatus() == OrderStatus.PENDING || order.getStatus() == OrderStatus.PAID)) {
                return true;
            }
        }
        return false;
    }
}