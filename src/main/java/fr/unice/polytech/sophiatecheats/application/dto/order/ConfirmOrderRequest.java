package fr.unice.polytech.sophiatecheats.application.dto.order;

/**
 * Request DTO pour confirmer une commande.
 * @param orderId l'identifiant de la commande à confirmer
 */
public record ConfirmOrderRequest(
    String orderId
) {
    /**
     * Valide que tous les champs obligatoires sont présents.
     *
     * @return true si la requête est valide
     */
    public boolean isValid() {
        return orderId != null && !orderId.trim().isEmpty();
    }
}
