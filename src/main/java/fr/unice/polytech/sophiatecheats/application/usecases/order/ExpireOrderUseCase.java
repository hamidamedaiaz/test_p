package fr.unice.polytech.sophiatecheats.application.usecases.order;

import fr.unice.polytech.sophiatecheats.application.usecases.UseCase;
import fr.unice.polytech.sophiatecheats.domain.entities.order.Order;
import fr.unice.polytech.sophiatecheats.domain.enums.OrderStatus;
import fr.unice.polytech.sophiatecheats.domain.exceptions.EntityNotFoundException;
import fr.unice.polytech.sophiatecheats.domain.exceptions.ValidationException;
import fr.unice.polytech.sophiatecheats.domain.repositories.OrderRepository;

import java.util.Optional;

/**
 * Use case pour expirer une commande et libérer son créneau de livraison.
 */
public class ExpireOrderUseCase implements UseCase<String, Void> {
    private final OrderRepository orderRepository;

    public ExpireOrderUseCase(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public Void execute(String orderId) {
        if (orderId == null || orderId.isBlank()) {
            throw new IllegalArgumentException("orderId ne peut pas être null ou vide");
        }
        Optional<Order> orderOpt = orderRepository.findById(orderId);
        Order order = orderOpt.orElseThrow(() -> new EntityNotFoundException("Commande non trouvée: " + orderId));
        if (order.getStatus() == OrderStatus.EXPIRED) {
            throw new ValidationException("La commande est déjà expirée");
        }
        order.expire(); // Met le statut à EXPIRED et libère le créneau
        orderRepository.save(order);
        return null;
    }
}

