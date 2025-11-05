package fr.unice.polytech.sophiatecheats.application.usecases.order;

import fr.unice.polytech.sophiatecheats.application.dto.order.PlaceOrderRequest;
import fr.unice.polytech.sophiatecheats.application.dto.order.PlaceOrderResponse;
import fr.unice.polytech.sophiatecheats.domain.entities.order.Order;
import fr.unice.polytech.sophiatecheats.domain.entities.restaurant.Dish;
import fr.unice.polytech.sophiatecheats.domain.entities.restaurant.Restaurant;
import fr.unice.polytech.sophiatecheats.domain.entities.user.User;
import fr.unice.polytech.sophiatecheats.domain.enums.DishCategory;
import fr.unice.polytech.sophiatecheats.domain.enums.OrderStatus;
import fr.unice.polytech.sophiatecheats.domain.enums.PaymentMethod;
import fr.unice.polytech.sophiatecheats.domain.exceptions.EntityNotFoundException;
import fr.unice.polytech.sophiatecheats.domain.exceptions.InsufficientCreditException;
import fr.unice.polytech.sophiatecheats.domain.repositories.OrderRepository;
import fr.unice.polytech.sophiatecheats.domain.repositories.Repository;
import fr.unice.polytech.sophiatecheats.domain.repositories.RestaurantRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PlaceOrderUseCaseTest {

    @Mock
    private Repository<User, UUID> userRepository;

    @Mock
    private RestaurantRepository restaurantRepository;

    @Mock
    private OrderRepository orderRepository;

    private PlaceOrderUseCase placeOrderUseCase;

    private User user;
    private Restaurant restaurant;
    private Dish dish;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        placeOrderUseCase = new PlaceOrderUseCase(userRepository, restaurantRepository, orderRepository);

        user = new User("test@example.com", "Test User");
        restaurant = new Restaurant("Test Restaurant", "Test Address");
        dish = new Dish("Test Dish", "Description", BigDecimal.valueOf(10.00), DishCategory.MAIN_COURSE);
        restaurant.addDish(dish);
    }

    @Test
    void shouldPlaceOrderSuccessfullyWithStudentCredit() {
        // Given
        UUID userId = user.getId();
        UUID restaurantId = restaurant.getId();
        UUID dishId = dish.getId();

        PlaceOrderRequest request = new PlaceOrderRequest(
            userId,
            restaurantId,
            List.of(new PlaceOrderRequest.OrderItemRequest(dishId, 1)),
            PaymentMethod.STUDENT_CREDIT
        );

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.of(restaurant));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        BigDecimal initialCredit = user.getCredit();

        // When
        PlaceOrderResponse response = placeOrderUseCase.execute(request);

        // Then
        assertNotNull(response);
        assertEquals(user.getName(), response.customerName());
        assertEquals(restaurant.getName(), response.restaurantName());
        assertEquals(BigDecimal.valueOf(10.00), response.totalAmount());
        assertEquals(OrderStatus.PAID, response.status());
        assertEquals(PaymentMethod.STUDENT_CREDIT, response.paymentMethod());

        // Vérifier que le crédit a été débité
        assertEquals(initialCredit.subtract(BigDecimal.valueOf(10.00)), user.getCredit());

        verify(userRepository).findById(userId);
        verify(restaurantRepository).findById(restaurantId);
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void shouldThrowInsufficientCreditExceptionWhenNotEnoughCredit() {
        // Given
        User poorUser = new User("poor@example.com", "Poor User");
        // Débiter tout le crédit sauf 1 euro
        poorUser.debitCredit(BigDecimal.valueOf(19.00));

        UUID userId = poorUser.getId();
        UUID restaurantId = restaurant.getId();
        UUID dishId = dish.getId();

        PlaceOrderRequest request = new PlaceOrderRequest(
            userId,
            restaurantId,
            List.of(new PlaceOrderRequest.OrderItemRequest(dishId, 1)),
            PaymentMethod.STUDENT_CREDIT
        );

        when(userRepository.findById(userId)).thenReturn(Optional.of(poorUser));
        when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.of(restaurant));

        // When & Then
        assertThrows(InsufficientCreditException.class, 
            () -> placeOrderUseCase.execute(request));

        verify(userRepository).findById(userId);
        verify(restaurantRepository).findById(restaurantId);
        verifyNoInteractions(orderRepository);
    }

    @Test
    void shouldThrowEntityNotFoundExceptionWhenUserNotFound() {
        // Given
        UUID userId = UUID.randomUUID();
        UUID restaurantId = restaurant.getId();
        UUID dishId = dish.getId();

        PlaceOrderRequest request = new PlaceOrderRequest(
            userId,
            restaurantId,
            List.of(new PlaceOrderRequest.OrderItemRequest(dishId, 1)),
            PaymentMethod.STUDENT_CREDIT
        );

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(EntityNotFoundException.class, 
            () -> placeOrderUseCase.execute(request));

        verify(userRepository).findById(userId);
        verifyNoInteractions(restaurantRepository);
        verifyNoInteractions(orderRepository);
    }

    @Test
    void shouldThrowEntityNotFoundExceptionWhenRestaurantNotFound() {
        // Given
        UUID userId = user.getId();
        UUID restaurantId = UUID.randomUUID();
        UUID dishId = dish.getId();

        PlaceOrderRequest request = new PlaceOrderRequest(
            userId,
            restaurantId,
            List.of(new PlaceOrderRequest.OrderItemRequest(dishId, 1)),
            PaymentMethod.STUDENT_CREDIT
        );

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(EntityNotFoundException.class, 
            () -> placeOrderUseCase.execute(request));

        verify(userRepository).findById(userId);
        verify(restaurantRepository).findById(restaurantId);
        verifyNoInteractions(orderRepository);
    }

    @Test
    void shouldThrowIllegalArgumentExceptionWhenRequestIsInvalid() {
        // Given
        PlaceOrderRequest invalidRequest = null;

        // When & Then
        assertThrows(IllegalArgumentException.class, 
            () -> placeOrderUseCase.execute(invalidRequest));

        verifyNoInteractions(userRepository);
        verifyNoInteractions(restaurantRepository);
        verifyNoInteractions(orderRepository);
    }
}