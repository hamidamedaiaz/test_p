package fr.unice.polytech.sophiatecheats.domain.services.payment;

import fr.unice.polytech.sophiatecheats.domain.entities.user.User;
import fr.unice.polytech.sophiatecheats.domain.enums.PaymentMethod;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitaires pour les stratégies de paiement (Strategy Pattern).
 *
 * Ces tests valident le bon fonctionnement du Strategy Pattern pour les paiements,
 * incluant toutes les stratégies concrètes et la factory.
 *
 * @author SophiaTech Eats Team
 * @version 1.0
 */
class PaymentStrategyTest {

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User("test@example.com", "Test User");
        testUser.setStudentCredit(BigDecimal.valueOf(50)); // Crédit initial de 50€
    }

    // ========== Tests StudentCreditStrategy ==========

    @Test
    void studentCredit_should_process_payment_successfully_when_sufficient_balance() {
        // Given
        PaymentStrategy strategy = new StudentCreditStrategy();
        BigDecimal amount = new BigDecimal("20.00");

        // When
        PaymentResult result = strategy.processPayment(amount, testUser);

        // Then
        assertTrue(result.success());
        assertEquals(amount, result.processedAmount());
        assertEquals(30.0, testUser.getStudentCredit().shortValueExact(), 0.01); // 50 - 20 = 30
        assertNotNull(result.transactionId());
        assertTrue(result.transactionId().startsWith("STU-"));
    }

    @Test
    void studentCredit_should_fail_when_insufficient_balance() {
        // Given
        PaymentStrategy strategy = new StudentCreditStrategy();
        BigDecimal amount = new BigDecimal("60.00"); // Plus que le solde disponible

        // When
        PaymentResult result = strategy.processPayment(amount, testUser);

        // Then
        assertFalse(result.success());
        assertEquals("INSUFFICIENT_FUNDS", result.errorCode());
        assertEquals(50.0, testUser.getStudentCredit().shortValueExact(), 0.01); // Solde inchangé
    }

    @Test
    void studentCredit_should_fail_with_invalid_amount() {
        // Given
        PaymentStrategy strategy = new StudentCreditStrategy();

        // When - Montant négatif
        PaymentResult result1 = strategy.processPayment(new BigDecimal("-10.00"), testUser);
        // When - Montant zéro
        PaymentResult result2 = strategy.processPayment(BigDecimal.ZERO, testUser);

        // Then
        assertFalse(result1.success());
        assertEquals("INVALID_AMOUNT", result1.errorCode());
        assertFalse(result2.success());
        assertEquals("INVALID_AMOUNT", result2.errorCode());
    }

    @Test
    void studentCredit_should_fail_with_null_user() {
        // Given
        PaymentStrategy strategy = new StudentCreditStrategy();

        // When
        PaymentResult result = strategy.processPayment(new BigDecimal("10.00"), null);

        // Then
        assertFalse(result.success());
        assertEquals("INVALID_USER", result.errorCode());
    }

    @Test
    void studentCredit_canPay_should_return_true_when_sufficient_balance() {
        // Given
        PaymentStrategy strategy = new StudentCreditStrategy();
        BigDecimal amount = new BigDecimal("30.00");

        // When
        boolean canPay = strategy.canPay(testUser, amount);

        // Then
        assertTrue(canPay);
    }

    @Test
    void studentCredit_canPay_should_return_false_when_insufficient_balance() {
        // Given
        PaymentStrategy strategy = new StudentCreditStrategy();
        BigDecimal amount = new BigDecimal("100.00");

        // When
        boolean canPay = strategy.canPay(testUser, amount);

        // Then
        assertFalse(canPay);
    }

    @Test
    void studentCredit_should_always_be_available() {
        // Given
        PaymentStrategy strategy = new StudentCreditStrategy();

        // When & Then
        assertTrue(strategy.isAvailable());
    }

    @Test
    void studentCredit_should_have_correct_name() {
        // Given
        PaymentStrategy strategy = new StudentCreditStrategy();

        // When & Then
        assertEquals("Student Credit Payment", strategy.getStrategyName());
    }

    // ========== Tests ExternalCardStrategy ==========

    @Test
    void externalCard_should_process_payment_successfully() {
        // Given
        ExternalCardStrategy strategy = new ExternalCardStrategy();
        strategy.setAlwaysSucceed(true); // Mode test : pas d'échec aléatoire
        BigDecimal amount = new BigDecimal("25.00");

        // When
        PaymentResult result = strategy.processPayment(amount, testUser);

        // Then
        assertTrue(result.success());
        assertEquals(amount, result.processedAmount());
        assertNotNull(result.transactionId());
        assertTrue(result.transactionId().startsWith("EXT-"));
    }

    @Test
    void externalCard_should_fail_when_amount_too_low() {
        // Given
        PaymentStrategy strategy = new ExternalCardStrategy();
        BigDecimal amount = new BigDecimal("0.001"); // Moins que le minimum

        // When
        PaymentResult result = strategy.processPayment(amount, testUser);

        // Then
        assertFalse(result.success());
        assertEquals("AMOUNT_TOO_LOW", result.errorCode());
    }

    @Test
    void externalCard_should_fail_when_amount_too_high() {
        // Given
        PaymentStrategy strategy = new ExternalCardStrategy();
        BigDecimal amount = new BigDecimal("600.00"); // Plus que le maximum (500€)

        // When
        PaymentResult result = strategy.processPayment(amount, testUser);

        // Then
        assertFalse(result.success());
        assertEquals("AMOUNT_TOO_HIGH", result.errorCode());
    }

    @Test
    void externalCard_should_fail_when_service_unavailable() {
        // Given
        ExternalCardStrategy strategy = new ExternalCardStrategy();
        strategy.setServiceAvailable(false); // Simuler l'indisponibilité

        // When
        PaymentResult result = strategy.processPayment(new BigDecimal("10.00"), testUser);

        // Then
        assertFalse(result.success());
        assertEquals("SERVICE_UNAVAILABLE", result.errorCode());
    }

    @Test
    void externalCard_canPay_should_respect_limits() {
        // Given
        PaymentStrategy strategy = new ExternalCardStrategy();

        // When & Then
        assertTrue(strategy.canPay(testUser, new BigDecimal("100.00"))); // Dans les limites
        assertFalse(strategy.canPay(testUser, new BigDecimal("0.001"))); // Trop bas
        assertFalse(strategy.canPay(testUser, new BigDecimal("600.00"))); // Trop haut
    }

    @Test
    void externalCard_should_have_correct_name() {
        // Given
        PaymentStrategy strategy = new ExternalCardStrategy();

        // When & Then
        assertEquals("External Card Payment", strategy.getStrategyName());
    }

    @Test
    void externalCard_should_have_random_failures_in_production_mode() {
        // Given
        ExternalCardStrategy strategy = new ExternalCardStrategy();
        // alwaysSucceed = false par défaut (mode production)
        BigDecimal amount = new BigDecimal("25.00");

        // When - Faire plusieurs tentatives pour vérifier le comportement aléatoire
        int attempts = 1000;
        int failures = 0;

        for (int i = 0; i < attempts; i++) {
            PaymentResult result = strategy.processPayment(amount, testUser);
            if (!result.success() && "CARD_DECLINED".equals(result.errorCode())) {
                failures++;
            }
        }

        // Then - On devrait avoir environ 5% d'échecs (entre 3% et 7% avec marge)
        double failureRate = (double) failures / attempts;
        assertTrue(failureRate > 0.03 && failureRate < 0.07,
            "Taux d'échec attendu ~5%, obtenu: " + (failureRate * 100) + "%");
    }

    // ========== Tests PaymentStrategyFactory ==========

    @Test
    void factory_should_create_student_credit_strategy() {
        // When
        PaymentStrategy strategy = PaymentStrategyFactory.createStrategy(PaymentMethod.STUDENT_CREDIT);

        // Then
        assertNotNull(strategy);
        assertInstanceOf(StudentCreditStrategy.class, strategy);
    }

    @Test
    void factory_should_create_external_card_strategy() {
        // When
        PaymentStrategy strategy = PaymentStrategyFactory.createStrategy(PaymentMethod.EXTERNAL_CARD);

        // Then
        assertNotNull(strategy);
        assertInstanceOf(ExternalCardStrategy.class, strategy);
    }

    @Test
    void factory_should_throw_exception_for_null_method() {
        // When & Then
        assertThrows(IllegalArgumentException.class,
            () -> PaymentStrategyFactory.createStrategy(null));
    }

    @Test
    void factory_should_indicate_supported_methods() {
        // When & Then
        assertTrue(PaymentStrategyFactory.isSupported(PaymentMethod.STUDENT_CREDIT));
        assertTrue(PaymentStrategyFactory.isSupported(PaymentMethod.EXTERNAL_CARD));
        assertFalse(PaymentStrategyFactory.isSupported(null));
    }

    @Test
    void factory_should_return_all_supported_methods() {
        // When
        PaymentMethod[] methods = PaymentStrategyFactory.getSupportedMethods();

        // Then
        assertEquals(2, methods.length);
        assertTrue(contains(methods, PaymentMethod.STUDENT_CREDIT));
        assertTrue(contains(methods, PaymentMethod.EXTERNAL_CARD));
    }

    // Helper method for array contains check
    private boolean contains(PaymentMethod[] array, PaymentMethod method) {
        for (PaymentMethod m : array) {
            if (m == method) return true;
        }
        return false;
    }
}

