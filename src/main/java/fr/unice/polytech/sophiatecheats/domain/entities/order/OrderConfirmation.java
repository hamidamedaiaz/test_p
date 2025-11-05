package fr.unice.polytech.sophiatecheats.domain.entities.order;

import fr.unice.polytech.sophiatecheats.domain.entities.Entity;
import fr.unice.polytech.sophiatecheats.domain.exceptions.ValidationException;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Entité représentant une confirmation de commande.
 */
@Getter
public class OrderConfirmation implements Entity<UUID> {

    private final UUID id;
    private final UUID orderId;
    private final UUID userId;
    private final String userEmail;
    private final String confirmationNumber;
    private final List<OrderItem> items;
    private final BigDecimal totalAmount;
    private final LocalDateTime estimatedDeliveryTime;
    private final String deliveryLocation;
    private final LocalDateTime createdAt;

    public OrderConfirmation(UUID orderId, UUID userId, String userEmail,
                           List<OrderItem> items, BigDecimal totalAmount,
                           LocalDateTime estimatedDeliveryTime, String deliveryLocation) {
        // Validation préalable pour éviter les NullPointerException
        validateConstructorParameters(orderId, userId, userEmail, items, totalAmount,
                                    estimatedDeliveryTime, deliveryLocation);

        this.id = UUID.randomUUID();
        this.orderId = orderId;
        this.userId = userId;
        this.userEmail = userEmail;
        this.confirmationNumber = generateConfirmationNumber();
        this.items = List.copyOf(items); // Maintenant sûr car items n'est pas null
        this.totalAmount = totalAmount;
        this.estimatedDeliveryTime = estimatedDeliveryTime;
        this.deliveryLocation = deliveryLocation;
        this.createdAt = LocalDateTime.now();

        validate();
    }

    private void validateConstructorParameters(UUID orderId, UUID userId, String userEmail,
                                             List<OrderItem> items, BigDecimal totalAmount,
                                             LocalDateTime estimatedDeliveryTime, String deliveryLocation) {
        if (orderId == null) {
            throw new ValidationException("L'ID de commande ne peut pas être null");
        }
        if (userId == null) {
            throw new ValidationException("L'ID utilisateur ne peut pas être null");
        }
        if (userEmail == null || userEmail.trim().isEmpty()) {
            throw new ValidationException("L'email utilisateur ne peut pas être vide");
        }
        if (!isValidEmail(userEmail)) {
            throw new ValidationException("Format d'email invalide: " + userEmail);
        }
        if (items == null || items.isEmpty()) {
            throw new ValidationException("La liste des articles ne peut pas être vide");
        }
        if (totalAmount == null || totalAmount.compareTo(BigDecimal.ZERO) < 0) {
            throw new ValidationException("Le montant total doit être positif");
        }
        if (estimatedDeliveryTime == null) {
            throw new ValidationException("L'heure de livraison estimée ne peut pas être null");
        }
        if (deliveryLocation == null || deliveryLocation.trim().isEmpty()) {
            throw new ValidationException("Le lieu de livraison ne peut pas être vide");
        }
    }

    @Override
    public void validate() {
        validateConstructorParameters(orderId, userId, userEmail, items, totalAmount,
                                    estimatedDeliveryTime, deliveryLocation);
    }

    private String generateConfirmationNumber() {
        return "STE-" + System.currentTimeMillis() + "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private boolean isValidEmail(String email) {
        return email != null && email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }

}
