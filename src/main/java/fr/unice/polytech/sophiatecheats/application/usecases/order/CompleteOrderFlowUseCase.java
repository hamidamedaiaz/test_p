package fr.unice.polytech.sophiatecheats.application.usecases.order;

import fr.unice.polytech.sophiatecheats.application.dto.order.*;
import fr.unice.polytech.sophiatecheats.application.usecases.UseCase;

/**
 * Orchestrateur pour le flux complet: Order → Slot → Payment → Confirmation
 *
 * Cette classe démontre comment utiliser les trois use cases ensemble
 * pour implémenter le flux métier complet selon les spécifications.
 *
 * Flux:
 * 1. La commande est créée (PENDING)
 * 2. → Sélection et réservation d'un créneau
 * 3. → Traitement du paiement (avec timeout)
 * 4. → Confirmation de la commande
 *
 * En cas d'échec à n'importe quelle étape, le créneau est libéré.
 */
public class CompleteOrderFlowUseCase implements UseCase<CompleteOrderFlowRequest, CompleteOrderFlowResponse> {

    private final SelectDeliverySlotUseCase selectSlotUseCase;
    private final ProcessPaymentUseCase processPaymentUseCase;
    private final ConfirmOrderUseCase confirmOrderUseCase;

    public CompleteOrderFlowUseCase(SelectDeliverySlotUseCase selectSlotUseCase,
                                  ProcessPaymentUseCase processPaymentUseCase,
                                  ConfirmOrderUseCase confirmOrderUseCase) {
        this.selectSlotUseCase = selectSlotUseCase;
        this.processPaymentUseCase = processPaymentUseCase;
        this.confirmOrderUseCase = confirmOrderUseCase;
    }

    @Override
    public CompleteOrderFlowResponse execute(CompleteOrderFlowRequest request) {
        if (request == null || !request.isValid()) {
            throw new IllegalArgumentException("Invalid complete order flow request");
        }

        try {
            // Étape 1: Sélectionner et réserver le créneau
            SelectDeliverySlotRequest slotRequest = new SelectDeliverySlotRequest(
                request.orderId(),
                request.slotId()
            );
            SelectDeliverySlotResponse slotResponse = selectSlotUseCase.execute(slotRequest);

            // Étape 2: Traiter le paiement
            ProcessPaymentRequest paymentRequest = new ProcessPaymentRequest(
                request.orderId(),
                request.cardToken()
            );
            ProcessPaymentResponse paymentResponse = processPaymentUseCase.execute(paymentRequest);

            if (!paymentResponse.success()) {
                return new CompleteOrderFlowResponse(
                    request.orderId(),
                    false,
                    "Payment failed: " + paymentResponse.message(),
                    null,
                    null,
                    null
                );
            }

            // Étape 3: Confirmer la commande
            ConfirmOrderRequest confirmRequest = new ConfirmOrderRequest(request.orderId());
            ConfirmOrderResponse confirmResponse = confirmOrderUseCase.execute(confirmRequest);

            return new CompleteOrderFlowResponse(
                request.orderId(),
                true,
                "Order completed successfully",
                slotResponse.slotStartTime(),
                paymentResponse.paymentMethod(),
                confirmResponse.estimatedDeliveryTime()
            );

        } catch (Exception e) {
            // En cas d'erreur, le créneau sera libéré automatiquement
            // par les use cases individuels
            return new CompleteOrderFlowResponse(
                request.orderId(),
                false,
                "Order flow failed: " + e.getMessage(),
                null,
                null,
                null
            );
        }
    }
}
