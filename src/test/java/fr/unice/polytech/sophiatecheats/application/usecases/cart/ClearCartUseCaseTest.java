package fr.unice.polytech.sophiatecheats.application.usecases.cart;

import fr.unice.polytech.sophiatecheats.application.dto.user.response.ClearCartResponse;
import fr.unice.polytech.sophiatecheats.domain.entities.cart.Cart;
import fr.unice.polytech.sophiatecheats.domain.repositories.CartRepository;
import fr.unice.polytech.sophiatecheats.domain.exceptions.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Tests unitaires pour ClearCartUseCase.
 *
 * <p>Ces tests valident le comportement du use case pour vider complètement
 * le panier sans le supprimer.</p>
 *
 * @author SophiaTech Eats Backend Team
 * @since 1.0
 */
@DisplayName("ClearCartUseCase - Tests Unitaires")
class ClearCartUseCaseTest {

    @Mock
    private CartRepository cartRepository;

    private ClearCartUseCase useCase;

    private UUID userId;
    private UUID cartId;
    private Cart testCart;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        useCase = new ClearCartUseCase(cartRepository);

        // Données de test
        userId = UUID.randomUUID();
        cartId = UUID.randomUUID();
        testCart = mock(Cart.class);
        when(testCart.getId()).thenReturn(cartId);
        when(testCart.getUserId()).thenReturn(userId);
    }


    @Test
    @DisplayName("Devrait vider le panier avec succès quand il contient des items")
    void should_clear_cart_successfully_when_cart_has_items() {
        // Given - Marcel a un panier actif
        when(cartRepository.findActiveCartByUserId(userId))
            .thenReturn(Optional.of(testCart));
        doNothing().when(testCart).clear();
        when(cartRepository.save(testCart)).thenReturn(testCart);

        // When - Marcel vide son panier
        ClearCartResponse response = useCase.execute(userId);

        // Then - Le panier est vidé
        assertNotNull(response, "La réponse ne devrait pas être nulle");
        assertTrue(response.success(), "L'opération devrait réussir");
        assertEquals(cartId, response.cartId(), "L'ID du panier devrait correspondre");
        assertEquals(0, response.itemCount(), "Le panier devrait contenir 0 items");
        assertEquals(BigDecimal.ZERO, response.totalAmount(), "Le montant total devrait être 0");
        assertEquals("Panier vidé avec succès", response.message(),
            "Le message de succès devrait être correct");

        // Vérifier que les méthodes du repository ont été appelées
        verify(cartRepository, times(1)).findActiveCartByUserId(userId);
        verify(testCart, times(1)).clear();
        verify(cartRepository, times(1)).save(testCart);
    }

    @Test
    @DisplayName("Devrait retourner itemCount = 0 après vidage")
    void should_return_zero_item_count_after_clearing() {
        // Given
        when(cartRepository.findActiveCartByUserId(userId))
            .thenReturn(Optional.of(testCart));
        when(cartRepository.save(testCart)).thenReturn(testCart);

        // When
        ClearCartResponse response = useCase.execute(userId);

        // Then
        assertEquals(0, response.itemCount(),
            "Le nombre d'items devrait être 0 après vidage");
    }

    @Test
    @DisplayName("Devrait retourner totalAmount = 0 après vidage")
    void should_return_zero_total_amount_after_clearing() {
        // Given
        when(cartRepository.findActiveCartByUserId(userId))
            .thenReturn(Optional.of(testCart));
        when(cartRepository.save(testCart)).thenReturn(testCart);

        // When
        ClearCartResponse response = useCase.execute(userId);

        // Then
        assertEquals(BigDecimal.ZERO, response.totalAmount(),
            "Le montant total devrait être 0 après vidage");
    }

    @Test
    @DisplayName("Devrait sauvegarder le panier après vidage")
    void should_save_cart_after_clearing() {
        // Given
        when(cartRepository.findActiveCartByUserId(userId))
            .thenReturn(Optional.of(testCart));

        // When
        useCase.execute(userId);

        // Then
        verify(cartRepository, times(1)).save(testCart);
    }


    @Test
    @DisplayName("Devrait retourner une erreur quand le panier n'existe pas")
    void should_return_error_when_cart_not_found() {
        // Given - Aucun panier actif pour Marcel
        when(cartRepository.findActiveCartByUserId(userId))
            .thenReturn(Optional.empty());

        // When - Marcel essaie de vider son panier
        ClearCartResponse response = useCase.execute(userId);

        // Then - Une erreur est retournée
        assertNotNull(response, "La réponse ne devrait pas être nulle");
        assertFalse(response.success(), "L'opération devrait échouer");
        assertNull(response.cartId(), "L'ID du panier devrait être null en cas d'erreur");
        assertEquals(0, response.itemCount(), "itemCount devrait être 0 en cas d'erreur");
        assertEquals(BigDecimal.ZERO, response.totalAmount(), "totalAmount devrait être 0 en cas d'erreur");
        assertTrue(response.message().contains("Panier introuvable"),
            "Le message devrait indiquer que le panier est introuvable");
        assertTrue(response.message().contains(userId.toString()),
            "Le message devrait contenir l'ID de l'utilisateur");

        // Vérifier qu'on n'a pas essayé de vider ou sauvegarder
        verify(cartRepository, times(1)).findActiveCartByUserId(userId);
        verify(testCart, never()).clear();
        verify(cartRepository, never()).save(any(Cart.class));
    }

    @Test
    @DisplayName("Devrait inclure l'ID utilisateur dans le message d'erreur")
    void should_include_user_id_in_error_message_when_cart_not_found() {
        // Given
        when(cartRepository.findActiveCartByUserId(userId))
            .thenReturn(Optional.empty());

        // When
        ClearCartResponse response = useCase.execute(userId);

        // Then
        String expectedMessage = "Aucun panier actif trouvé pour l'utilisateur: " + userId;
        assertTrue(response.message().contains(expectedMessage),
            "Le message d'erreur devrait contenir l'ID de l'utilisateur");
    }


    @Test
    @DisplayName("Devrait gérer une ValidationException lors du vidage")
    void should_handle_validation_exception_when_clearing_fails() {
        // Given - Une exception de validation survient
        when(cartRepository.findActiveCartByUserId(userId))
            .thenReturn(Optional.of(testCart));
        doThrow(new ValidationException("Le panier contient des items non valides"))
            .when(testCart).clear();

        // When
        ClearCartResponse response = useCase.execute(userId);

        // Then - L'erreur est capturée et retournée proprement
        assertNotNull(response, "La réponse ne devrait pas être nulle");
        assertFalse(response.success(), "L'opération devrait échouer");
        assertNull(response.cartId(), "L'ID devrait être null en cas d'erreur");
        assertTrue(response.message().contains("Erreur de validation"),
            "Le message devrait indiquer une erreur de validation");
        assertTrue(response.message().contains("items non valides"),
            "Le message devrait contenir les détails de l'exception");

        // Vérifier les appels
        verify(cartRepository, times(1)).findActiveCartByUserId(userId);
        verify(testCart, times(1)).clear();
        verify(cartRepository, never()).save(any(Cart.class));
    }

    @Test
    @DisplayName("Devrait gérer une RuntimeException lors de la sauvegarde")
    void should_handle_runtime_exception_when_save_fails() {
        // Given - Une exception survient lors de la sauvegarde
        when(cartRepository.findActiveCartByUserId(userId))
            .thenReturn(Optional.of(testCart));
        doNothing().when(testCart).clear();
        when(cartRepository.save(testCart))
            .thenThrow(new RuntimeException("Erreur base de données"));

        // When
        ClearCartResponse response = useCase.execute(userId);

        // Then
        assertFalse(response.success());
        assertTrue(response.message().contains("Erreur inattendue"));
        assertTrue(response.message().contains("Erreur base de données"));
    }

    @Test
    @DisplayName("Devrait gérer une NullPointerException")
    void should_handle_null_pointer_exception() {
        // Given
        when(cartRepository.findActiveCartByUserId(userId))
            .thenReturn(Optional.of(testCart));
        doThrow(new NullPointerException("Objet null inattendu"))
            .when(testCart).clear();

        // When
        ClearCartResponse response = useCase.execute(userId);

        // Then
        assertFalse(response.success());
        assertTrue(response.message().contains("Erreur inattendue"));
    }


    @Test
    @DisplayName("Ne devrait PAS appeler clear() si le panier n'existe pas")
    void should_not_call_clear_when_cart_not_found() {
        // Given
        when(cartRepository.findActiveCartByUserId(userId))
            .thenReturn(Optional.empty());

        // When
        useCase.execute(userId);

        // Then - clear() ne doit jamais être appelé
        verify(testCart, never()).clear();
    }

    @Test
    @DisplayName("Ne devrait PAS appeler save() si le panier n'existe pas")
    void should_not_call_save_when_cart_not_found() {
        // Given
        when(cartRepository.findActiveCartByUserId(userId))
            .thenReturn(Optional.empty());

        // When
        useCase.execute(userId);

        // Then
        verify(cartRepository, never()).save(any(Cart.class));
    }

    @Test
    @DisplayName("Devrait appeler findActiveCartByUserId() avec le bon userId")
    void should_call_find_with_correct_user_id() {
        // Given
        when(cartRepository.findActiveCartByUserId(userId))
            .thenReturn(Optional.of(testCart));

        // When
        useCase.execute(userId);

        // Then
        verify(cartRepository, times(1)).findActiveCartByUserId(userId);
    }

    @Test
    @DisplayName("Devrait appeler clear() sur le bon Cart")
    void should_call_clear_on_correct_cart() {
        // Given
        when(cartRepository.findActiveCartByUserId(userId))
            .thenReturn(Optional.of(testCart));

        // When
        useCase.execute(userId);

        // Then
        verify(testCart, times(1)).clear();
    }

    @Test
    @DisplayName("Devrait appeler save() avec le bon Cart")
    void should_call_save_with_correct_cart() {
        // Given
        when(cartRepository.findActiveCartByUserId(userId))
            .thenReturn(Optional.of(testCart));

        // When
        useCase.execute(userId);

        // Then
        verify(cartRepository, times(1)).save(testCart);
    }


    @Test
    @DisplayName("Devrait fonctionner avec un userId différent")
    void should_work_with_different_user_id() {
        // Given - Un autre utilisateur
        UUID anotherUserId = UUID.randomUUID();
        UUID anotherCartId = UUID.randomUUID();
        Cart anotherCart = mock(Cart.class);
        when(anotherCart.getId()).thenReturn(anotherCartId);
        when(cartRepository.findActiveCartByUserId(anotherUserId))
            .thenReturn(Optional.of(anotherCart));

        // When
        ClearCartResponse response = useCase.execute(anotherUserId);

        // Then
        assertTrue(response.success());
        assertEquals(anotherCartId, response.cartId());
    }

    @Test
    @DisplayName("Devrait retourner success=true uniquement en cas de vidage réussi")
    void should_return_success_true_only_when_clearing_succeeds() {
        // Given - Cas de succès
        when(cartRepository.findActiveCartByUserId(userId))
            .thenReturn(Optional.of(testCart));

        // When
        ClearCartResponse successResponse = useCase.execute(userId);

        // Then
        assertTrue(successResponse.success());

        // Given - Cas d'échec
        when(cartRepository.findActiveCartByUserId(userId))
            .thenReturn(Optional.empty());

        // When
        ClearCartResponse errorResponse = useCase.execute(userId);

        // Then
        assertFalse(errorResponse.success());
    }


    @Test
    @DisplayName("Devrait être idempotent - vider un panier déjà vide ne doit pas échouer")
    void should_be_idempotent_when_clearing_already_empty_cart() {
        // Given - Un panier déjà vide
        when(cartRepository.findActiveCartByUserId(userId))
            .thenReturn(Optional.of(testCart));
        doNothing().when(testCart).clear(); // clear() sur un panier vide ne fait rien

        // When - Premier appel
        ClearCartResponse firstResponse = useCase.execute(userId);

        // Then
        assertTrue(firstResponse.success());

        // When - Deuxième appel (panier déjà vide)
        ClearCartResponse secondResponse = useCase.execute(userId);

        // Then - Toujours un succès
        assertTrue(secondResponse.success());
        assertEquals("Panier vidé avec succès", secondResponse.message());
    }

    @Test
    @DisplayName("Le message de succès devrait être cohérent")
    void should_return_consistent_success_message() {
        // Given
        when(cartRepository.findActiveCartByUserId(userId))
            .thenReturn(Optional.of(testCart));

        // When
        ClearCartResponse response = useCase.execute(userId);

        // Then - Vérifier le message exact
        assertEquals("Panier vidé avec succès", response.message(),
            "Le message de succès doit être exactement celui attendu");
    }

    @Test
    @DisplayName("Devrait retourner les bonnes valeurs pour un panier vide")
    void should_return_correct_values_for_empty_cart() {
        // Given
        when(cartRepository.findActiveCartByUserId(userId))
            .thenReturn(Optional.of(testCart));

        // When
        ClearCartResponse response = useCase.execute(userId);

        // Then - Vérifier toutes les valeurs
        assertEquals(cartId, response.cartId(), "cartId devrait correspondre");
        assertEquals(0, response.itemCount(), "itemCount devrait être 0");
        assertEquals(BigDecimal.ZERO, response.totalAmount(), "totalAmount devrait être 0");
        assertTrue(response.success(), "success devrait être true");
        assertNotNull(response.message(), "message ne devrait pas être null");
    }
}

