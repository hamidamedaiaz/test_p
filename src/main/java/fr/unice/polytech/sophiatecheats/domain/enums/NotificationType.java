package fr.unice.polytech.sophiatecheats.domain.enums;

/**
 * Enumération des types de notification.
 */
public enum NotificationType {
    ORDER_CONFIRMATION("Confirmation de commande"),
    ORDER_PREPARING("Commande en préparation"),
    ORDER_OUT_FOR_DELIVERY("Commande en livraison"),
    ORDER_DELIVERED("Commande livrée"),
    ORDER_CANCELLED("Commande annulée");

    private final String displayName;

    NotificationType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
