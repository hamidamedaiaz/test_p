package fr.unice.polytech.sophiatecheats.application.usecases.cart;

import fr.unice.polytech.sophiatecheats.application.dto.user.CancelCartResponse;
import fr.unice.polytech.sophiatecheats.domain.entities.cart.Cart;
import fr.unice.polytech.sophiatecheats.domain.repositories.CartRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@DisplayName("CancelCartUseCase - Tests Unitaires")
class CancelCartUseCaseTest {

    @Mock
    private CartRepository cartRepository;

    private CancelCartUseCase useCase;

    private UUID userId;
    private UUID cartId;
    private Cart testCart;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        useCase = new CancelCartUseCase(cartRepository);

        // Données de test
        userId = UUID.randomUUID();
        cartId = UUID.randomUUID();
        testCart = mock(Cart.class);
        when(testCart.getId()).thenReturn(cartId);
        when(testCart.getUserId()).thenReturn(userId);
    }


    @Test
    @DisplayName("Devrait supprimer le panier avec succès quand il existe")
    void execute_ShouldDeleteCart_WhenCartExists() {
        // Given: Un panier actif existe
        when(cartRepository.findActiveCartByUserId(userId))
            .thenReturn(Optional.of(testCart));
        doNothing().when(cartRepository).delete(testCart);

        // When: On annule le panier
        CancelCartResponse response = useCase.execute(userId);

        // Then: Le panier est supprimé
        assertNotNull(response, "La réponse ne devrait pas être nulle");
        assertTrue(response.isSuccess(), "L'opération devrait réussir");
        assertEquals(cartId, response.getCancelledCartId(), "L'ID du panier supprimé devrait correspondre");
        assertEquals("Panier annulé et supprimé avec succès", response.getMessage(),
            "Le message de succès devrait être correct");

        // Vérifier que le repository a bien été appelé
        verify(cartRepository, times(1)).findActiveCartByUserId(userId);
        verify(cartRepository, times(1)).delete(testCart);
    }

    @Test
    @DisplayName("Devrait retourner l'ID correct du panier supprimé")
    void execute_ShouldReturnCorrectCartId_WhenDeleted() {
        // Given
        when(cartRepository.findActiveCartByUserId(userId))
            .thenReturn(Optional.of(testCart));

        // When
        CancelCartResponse response = useCase.execute(userId);

        // Then
        assertEquals(cartId, response.getCancelledCartId(),
            "L'ID retourné doit correspondre à l'ID du panier supprimé");
    }

    @Test
    @DisplayName("Devrait retourner une erreur quand le panier n'existe pas")
    void execute_ShouldReturnError_WhenCartNotFound() {
        // Given: Aucun panier actif pour cet utilisateur
        when(cartRepository.findActiveCartByUserId(userId))
            .thenReturn(Optional.empty());

        // When: On essaie d'annuler le panier
        CancelCartResponse response = useCase.execute(userId);

        // Then: Une erreur est retournée
        assertNotNull(response, "La réponse ne devrait pas être nulle");
        assertFalse(response.isSuccess(), "L'opération devrait échouer");
        assertNull(response.getCancelledCartId(), "L'ID du panier devrait être null en cas d'erreur");
        assertTrue(response.getMessage().contains("Panier introuvable"),
            "Le message devrait indiquer que le panier est introuvable");
        assertTrue(response.getMessage().contains(userId.toString()),
            "Le message devrait contenir l'ID de l'utilisateur");

        // Vérifier qu'on n'a pas essayé de supprimer
        verify(cartRepository, times(1)).findActiveCartByUserId(userId);
        verify(cartRepository, never()).delete(any(Cart.class));
    }

    @Test
    @DisplayName("Devrait inclure l'ID utilisateur dans le message d'erreur")
    void execute_ShouldIncludeUserIdInErrorMessage_WhenCartNotFound() {
        // Given
        when(cartRepository.findActiveCartByUserId(userId))
            .thenReturn(Optional.empty());

        // When
        CancelCartResponse response = useCase.execute(userId);

        // Then
        String expectedMessage = "Aucun panier actif trouvé pour l'utilisateur: " + userId;
        assertTrue(response.getMessage().contains(expectedMessage),
            "Le message d'erreur devrait contenir l'ID de l'utilisateur");
    }


    @Test
    @DisplayName("Devrait gérer une RuntimeException lors de la suppression")
    void execute_ShouldHandleRuntimeException_WhenDeleteFails() {
        // Given: Une exception survient lors de la suppression
        when(cartRepository.findActiveCartByUserId(userId))
            .thenReturn(Optional.of(testCart));
        doThrow(new RuntimeException("Erreur base de données"))
            .when(cartRepository).delete(testCart);

        // When
        CancelCartResponse response = useCase.execute(userId);

        // Then: L'erreur est capturée et retournée proprement
        assertNotNull(response, "La réponse ne devrait pas être nulle");
        assertFalse(response.isSuccess(), "L'opération devrait échouer");
        assertNull(response.getCancelledCartId(), "L'ID devrait être null en cas d'erreur");
        assertTrue(response.getMessage().contains("Erreur lors de la suppression"),
            "Le message devrait indiquer une erreur de suppression");
        assertTrue(response.getMessage().contains("Erreur base de données"),
            "Le message devrait contenir les détails de l'exception");

        // Vérifier les appels
        verify(cartRepository, times(1)).findActiveCartByUserId(userId);
        verify(cartRepository, times(1)).delete(testCart);
    }

    @Test
    @DisplayName("Devrait gérer une IllegalStateException")
    void execute_ShouldHandleIllegalStateException_WhenDeleteFails() {
        // Given
        when(cartRepository.findActiveCartByUserId(userId))
            .thenReturn(Optional.of(testCart));
        doThrow(new IllegalStateException("État invalide"))
            .when(cartRepository).delete(testCart);

        // When
        CancelCartResponse response = useCase.execute(userId);

        // Then
        assertFalse(response.isSuccess());
        assertTrue(response.getMessage().contains("Erreur lors de la suppression"));
    }



    @Test
    @DisplayName("Ne devrait PAS appeler delete() si le panier n'existe pas")
    void execute_ShouldNotCallDelete_WhenCartNotFound() {
        // Given
        when(cartRepository.findActiveCartByUserId(userId))
            .thenReturn(Optional.empty());

        // When
        useCase.execute(userId);

        // Then: delete() ne doit jamais être appelé
        verify(cartRepository, never()).delete(any(Cart.class));
    }

    @Test
    @DisplayName("Devrait appeler findActiveCartByUserId() avec le bon userId")
    void execute_ShouldCallFindWithCorrectUserId() {
        // Given
        when(cartRepository.findActiveCartByUserId(userId))
            .thenReturn(Optional.of(testCart));

        // When
        useCase.execute(userId);

        // Then
        verify(cartRepository, times(1)).findActiveCartByUserId(userId);
    }

    @Test
    @DisplayName("Devrait appeler delete() avec le bon Cart")
    void execute_ShouldCallDeleteWithCorrectCart() {
        // Given
        when(cartRepository.findActiveCartByUserId(userId))
            .thenReturn(Optional.of(testCart));

        // When
        useCase.execute(userId);

        // Then
        verify(cartRepository, times(1)).delete(testCart);
    }


    @Test
    @DisplayName("Devrait fonctionner avec un userId différent")
    void execute_ShouldWork_WithDifferentUserId() {
        // Given: Un autre utilisateur
        UUID anotherUserId = UUID.randomUUID();
        UUID anotherCartId = UUID.randomUUID();
        Cart anotherCart = mock(Cart.class);
        when(anotherCart.getId()).thenReturn(anotherCartId);
        when(cartRepository.findActiveCartByUserId(anotherUserId))
            .thenReturn(Optional.of(anotherCart));

        // When
        CancelCartResponse response = useCase.execute(anotherUserId);

        // Then
        assertTrue(response.isSuccess());
        assertEquals(anotherCartId, response.getCancelledCartId());
    }

    @Test
    @DisplayName("Devrait retourner success=true uniquement en cas de suppression réussie")
    void execute_ShouldReturnSuccessTrue_OnlyWhenDeletionSucceeds() {
        // Given: Cas de succès
        when(cartRepository.findActiveCartByUserId(userId))
            .thenReturn(Optional.of(testCart));

        // When
        CancelCartResponse response = useCase.execute(userId);

        // Then
        assertTrue(response.isSuccess());

        // Given: Cas d'échec
        when(cartRepository.findActiveCartByUserId(userId))
            .thenReturn(Optional.empty());

        // When
        CancelCartResponse errorResponse = useCase.execute(userId);

        // Then
        assertFalse(errorResponse.isSuccess());
    }


    @Test
    @DisplayName("Devrait être idempotent pour un panier déjà supprimé")
    void execute_ShouldBeIdempotent_WhenCartAlreadyDeleted() {
        // Given: Premier appel supprime le panier
        when(cartRepository.findActiveCartByUserId(userId))
            .thenReturn(Optional.of(testCart))
            .thenReturn(Optional.empty()); // Deuxième appel : panier déjà supprimé

        // When: Premier appel
        CancelCartResponse firstResponse = useCase.execute(userId);

        // Then
        assertTrue(firstResponse.isSuccess());

        // When: Deuxième appel (panier déjà supprimé)
        CancelCartResponse secondResponse = useCase.execute(userId);

        // Then: Retourne une erreur propre (pas de crash)
        assertFalse(secondResponse.isSuccess());
        assertTrue(secondResponse.getMessage().contains("introuvable"));
    }

    @Test
    @DisplayName("Le message de succès devrait être cohérent")
    void execute_ShouldReturnConsistentSuccessMessage() {
        // Given
        when(cartRepository.findActiveCartByUserId(userId))
            .thenReturn(Optional.of(testCart));

        // When
        CancelCartResponse response = useCase.execute(userId);

        // Then: Vérifier le message exact
        assertEquals("Panier annulé et supprimé avec succès", response.getMessage(),
            "Le message de succès doit être exactement celui attendu");
    }
}

