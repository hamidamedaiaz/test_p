package fr.unice.polytech.sophiatecheats.application.usecases.user.order;

import fr.unice.polytech.sophiatecheats.application.dto.user.request.PlaceOrderRequest;
import fr.unice.polytech.sophiatecheats.domain.entities.cart.Cart;
import fr.unice.polytech.sophiatecheats.domain.entities.restaurant.Restaurant;
import fr.unice.polytech.sophiatecheats.domain.entities.user.User;
import fr.unice.polytech.sophiatecheats.domain.exceptions.ValidationException;
import fr.unice.polytech.sophiatecheats.domain.repositories.CartRepository;
import fr.unice.polytech.sophiatecheats.domain.repositories.OrderRepository;
import fr.unice.polytech.sophiatecheats.domain.repositories.RestaurantRepository;
import fr.unice.polytech.sophiatecheats.domain.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("Test du timeout du panier pour PlaceOrderUseCase")
class PlaceOrderUseCaseTimeoutTest {
    private UserRepository userRepository;
    private RestaurantRepository restaurantRepository;
    private OrderRepository orderRepository;
    private CartRepository cartRepository;
    private PlaceOrderUseCase useCase;
    private UUID userId;
    private UUID restaurantId;
    private User testUser;
    private Restaurant testRestaurant;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        restaurantRepository = mock(RestaurantRepository.class);
        orderRepository = mock(OrderRepository.class);
        cartRepository = mock(CartRepository.class);
        useCase = new PlaceOrderUseCase(userRepository, restaurantRepository, orderRepository, cartRepository);
        userId = UUID.randomUUID();
        restaurantId = UUID.randomUUID();
        testUser = mock(User.class);
        testRestaurant = mock(Restaurant.class);
    }

    @Test
    @DisplayName("Ne permet pas de commander si le panier a expiré (plus de 5 minutes)")
    void testCartTimeoutThrowsValidationException() {
        Cart expiredCart = mock(Cart.class);
        when(expiredCart.isEmpty()).thenReturn(false);
        when(expiredCart.getCreatedAt()).thenReturn(java.time.LocalDateTime.now().minusMinutes(6));
        when(cartRepository.findActiveCartByUserId(userId)).thenReturn(Optional.of(expiredCart));
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.of(testRestaurant));
        when(orderRepository.existsActiveOrderByUserId(userId)).thenReturn(false);
        PlaceOrderRequest request = mock(PlaceOrderRequest.class);
        when(request.userId()).thenReturn(userId);
        when(request.restaurantId()).thenReturn(restaurantId);
        when(request.isValid()).thenReturn(true);
        ValidationException ex = assertThrows(ValidationException.class, () -> useCase.execute(request));
        assertTrue(ex.getMessage().contains("délai pour valider votre panier est dépassé"));
    }
}

