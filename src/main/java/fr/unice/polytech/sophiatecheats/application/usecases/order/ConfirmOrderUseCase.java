package fr.unice.polytech.sophiatecheats.application.usecases.order;

import fr.unice.polytech.sophiatecheats.application.dto.order.request.ConfirmOrderRequest;
import fr.unice.polytech.sophiatecheats.application.dto.order.response.ConfirmOrderResponse;
import fr.unice.polytech.sophiatecheats.application.usecases.UseCase;
import fr.unice.polytech.sophiatecheats.domain.entities.order.Order;
import fr.unice.polytech.sophiatecheats.domain.exceptions.EntityNotFoundException;
import fr.unice.polytech.sophiatecheats.domain.exceptions.OrderAlreadyConfirmedException;
import fr.unice.polytech.sophiatecheats.domain.exceptions.OrderExpiredException;
import fr.unice.polytech.sophiatecheats.domain.repositories.OrderRepository;

/**
 * Use case pour confirmer une commande après paiement.
 *
 * <p>Ce use case confirme une commande existante et calcule le temps de livraison estimé.</p>
 *
 * <h3>Flux nominal:</h3>
 * <ol>
 *   <li>Récupère la commande par son ID</li>
 *   <li>Vérifie que la commande peut être confirmée</li>
 *   <li>Confirme la commande et calcule le temps de livraison</li>
 *   <li>Sauvegarde la commande mise à jour</li>
 *   <li>Retourne les détails de confirmation</li>
 * </ol>
 *
 * <h3>Règles métier:</h3>
 * <ul>
 *   <li>Une commande ne peut être confirmée que si elle est en statut PENDING ou PAID</li>
 *   <li>Une commande expirée ne peut pas être confirmée</li>
 *   <li>Le temps de livraison est fixé à 30 minutes après confirmation</li>
 * </ul>
 */
public class ConfirmOrderUseCase implements UseCase<ConfirmOrderRequest, ConfirmOrderResponse> {

    private final OrderRepository orderRepository;

    public ConfirmOrderUseCase(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public ConfirmOrderResponse execute(ConfirmOrderRequest request) {
        if (request == null || !request.isValid()) {
            throw new IllegalArgumentException("Invalid request");
        }

        // Récupérer la commande
        Order order = orderRepository.findById(request.orderId())
            .orElseThrow(() -> new EntityNotFoundException("Order not found: " + request.orderId()));

        // Vérifier que la commande peut être confirmée
        if (!order.canBeConfirmed()) {
            switch (order.getStatus()) {
                case CONFIRMED -> throw new OrderAlreadyConfirmedException(order.getOrderId());
                case EXPIRED -> throw new OrderExpiredException(order.getOrderId());
                default -> throw new IllegalStateException("Cannot confirm order in status: " + order.getStatus());
            }
        }

        // Confirmer la commande
        order.confirm();

        // Sauvegarder la commande mise à jour
        Order confirmedOrder = orderRepository.save(order);

        // Retourner la réponse
        return new ConfirmOrderResponse(
            confirmedOrder.getOrderId(),
            confirmedOrder.getUser().getName(),
            confirmedOrder.getRestaurant().getName(),
            confirmedOrder.getTotalAmount(),
            confirmedOrder.getStatus(),
            confirmedOrder.getOrderDateTime(),
            confirmedOrder.getDeliveryTime()
        );
    }
}
