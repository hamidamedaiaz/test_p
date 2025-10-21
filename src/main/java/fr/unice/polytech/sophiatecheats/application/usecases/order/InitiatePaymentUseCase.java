package fr.unice.polytech.sophiatecheats.application.usecases.order;

import fr.unice.polytech.sophiatecheats.domain.entities.order.Order;
import fr.unice.polytech.sophiatecheats.domain.enums.OrderStatus;
import fr.unice.polytech.sophiatecheats.domain.exceptions.EntityNotFoundException;
import fr.unice.polytech.sophiatecheats.domain.exceptions.ValidationException;
import fr.unice.polytech.sophiatecheats.domain.repositories.OrderRepository;

/**
 * Use case pour démarrer le timeout de paiement après choix du créneau.
 */
public class InitiatePaymentUseCase {
    private final OrderRepository orderRepository;

    public InitiatePaymentUseCase(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    /**
     * Démarre le chrono de paiement pour la commande donnée.
     * @param orderId l'identifiant de la commande
     */
    public void execute(String orderId) {
        if (orderId == null || orderId.isBlank()) {
            throw new IllegalArgumentException("orderId ne peut pas être null ou vide");
        }
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Commande non trouvée: " + orderId));
        if (!order.hasDeliverySlot()) {
            throw new ValidationException("Impossible de démarrer le paiement: aucun créneau réservé");
        }
        if (order.getStatus() == OrderStatus.PAID || order.getStatus() == OrderStatus.EXPIRED) {
            throw new ValidationException("Impossible de démarrer le paiement: commande déjà payée ou expirée");
        }
        order.startPaymentTimeout();
        orderRepository.save(order);
    }
}

