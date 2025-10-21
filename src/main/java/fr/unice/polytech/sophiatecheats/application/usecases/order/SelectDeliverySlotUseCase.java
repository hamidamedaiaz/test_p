package fr.unice.polytech.sophiatecheats.application.usecases.order;

import fr.unice.polytech.sophiatecheats.application.dto.order.SelectDeliverySlotRequest;
import fr.unice.polytech.sophiatecheats.application.dto.order.SelectDeliverySlotResponse;
import fr.unice.polytech.sophiatecheats.application.usecases.UseCase;
import fr.unice.polytech.sophiatecheats.domain.entities.order.Order;
import fr.unice.polytech.sophiatecheats.domain.entities.restaurant.Restaurant;
import fr.unice.polytech.sophiatecheats.domain.entities.restaurant.TimeSlot;
import fr.unice.polytech.sophiatecheats.domain.exceptions.EntityNotFoundException;
import fr.unice.polytech.sophiatecheats.domain.exceptions.SlotNotFoundException;
import fr.unice.polytech.sophiatecheats.domain.repositories.OrderRepository;
import fr.unice.polytech.sophiatecheats.domain.repositories.RestaurantRepository;

import java.util.Optional;
import java.util.UUID;

/**
 * Use case pour sélectionner et réserver un créneau de livraison pour une commande.
 *
 * Ce use case implémente la deuxième étape du flux: Order → Slot → Payment
 *
 * Flux:
 * 1. Vérifie que la commande existe et n'a pas déjà de créneau
 * 2. Trouve le créneau demandé dans le restaurant
 * 3. Réserve le créneau
 * 4. Associe le créneau à la commande
 * 5. Sauvegarde la commande mise à jour
 */
public class SelectDeliverySlotUseCase implements UseCase<SelectDeliverySlotRequest, SelectDeliverySlotResponse> {

    private final OrderRepository orderRepository;
    private final RestaurantRepository restaurantRepository;

    public SelectDeliverySlotUseCase(OrderRepository orderRepository, RestaurantRepository restaurantRepository) {
        this.orderRepository = orderRepository;
        this.restaurantRepository = restaurantRepository;
    }

    @Override
    public SelectDeliverySlotResponse execute(SelectDeliverySlotRequest request) {
        if (request == null || !request.isValid()) {
            throw new IllegalArgumentException("Invalid request");
        }

        // 1. Récupérer la commande
        Order order = orderRepository.findById(request.orderId())
            .orElseThrow(() -> new EntityNotFoundException("Order not found: " + request.orderId()));

        // 2. Vérifier que la commande n'a pas déjà de créneau
        if (order.hasDeliverySlot()) {
            throw new IllegalStateException("Order already has a delivery slot assigned");
        }

        // 3. Récupérer le restaurant et son planning de livraison
        Restaurant restaurant = restaurantRepository.findById(order.getRestaurant().getId())
            .orElseThrow(() -> new EntityNotFoundException("Restaurant not found"));

        // 4. Trouver et réserver le créneau
        Optional<TimeSlot> slotOpt = restaurant.getDeliverySchedule().findSlotById(request.slotId());
        if (slotOpt.isEmpty()) {
            throw new SlotNotFoundException("Slot not found: " + request.slotId());
        }

        TimeSlot slot = slotOpt.get();

        // 5. Vérifier que le créneau est disponible et le réserver
        if (!slot.isAvailable()) {
            throw new SlotNotFoundException("Slot " + request.slotId() + " is no longer available");
        }

        try {
            slot.reserveOrThrow();
        } catch (Exception e) {
            throw new SlotNotFoundException("Failed to reserve slot " + request.slotId() + ": " + e.getMessage());
        }

        // 6. Associer le créneau à la commande
        order.assignDeliverySlot(slot.getId(), slot.getStartTime());

        // 7. Sauvegarder la commande mise à jour
        Order updatedOrder = orderRepository.save(order);

        // 8. Retourner la réponse
        return new SelectDeliverySlotResponse(
            updatedOrder.getOrderId(),
            slot.getId(),
            slot.getStartTime(),
            slot.getEndTime(),
            "Slot successfully reserved for order"
        );
    }
}
