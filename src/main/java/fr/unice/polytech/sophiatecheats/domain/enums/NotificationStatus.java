package fr.unice.polytech.sophiatecheats.domain.enums;

/**
 * Enumération des statuts de notification.
 */
public enum NotificationStatus {
    PENDING("En attente"),
    SENT("Envoyé"),
    DELIVERED("Livré"),
    FAILED("Échec"),
    RETRY("Nouvelle tentative");

    private final String displayName;

    NotificationStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
