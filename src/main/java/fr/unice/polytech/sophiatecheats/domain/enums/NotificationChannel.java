package fr.unice.polytech.sophiatecheats.domain.enums;

/**
 * Enumération des canaux de notification disponibles.
 */
public enum NotificationChannel {
    EMAIL("Email"),
    SMS("SMS"),
    PUSH("Push Notification");

    private final String displayName;

    NotificationChannel(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
