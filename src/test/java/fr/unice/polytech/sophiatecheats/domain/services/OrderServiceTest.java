package fr.unice.polytech.sophiatecheats.domain.services;

import fr.unice.polytech.sophiatecheats.domain.entities.cart.Cart;
import fr.unice.polytech.sophiatecheats.domain.entities.order.Order;
import fr.unice.polytech.sophiatecheats.domain.entities.restaurant.Dish;
import fr.unice.polytech.sophiatecheats.domain.entities.restaurant.Restaurant;
import fr.unice.polytech.sophiatecheats.domain.entities.restaurant.Schedule;
import fr.unice.polytech.sophiatecheats.domain.entities.restaurant.TimeSlot;
import fr.unice.polytech.sophiatecheats.domain.entities.user.User;
import fr.unice.polytech.sophiatecheats.domain.enums.OrderStatus;
import fr.unice.polytech.sophiatecheats.domain.exceptions.InsufficientCreditException;
import fr.unice.polytech.sophiatecheats.domain.exceptions.InvalidCartOperationException;
import fr.unice.polytech.sophiatecheats.domain.repositories.CartRepository;
import fr.unice.polytech.sophiatecheats.domain.repositories.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrderServiceTest {

    private OrderRepository orderRepo;
    private CartRepository cartRepo;
    private OrderService orderService;

    @BeforeEach
    void setUp() {
        orderRepo = Mockito.mock(OrderRepository.class);
        cartRepo = Mockito.mock(CartRepository.class);
        CartService cartService = new CartService(cartRepo);
        orderService = new OrderService(orderRepo, cartService);
    }

    @Test
    void testCreateOrderSuccessfully() {
        User user = new User("u@mail.com", "User");
        Cart cart = new Cart(user.getId());
        Dish dish = Dish.builder().name("Pizza").description("Delicious").price(BigDecimal.valueOf(10)).build();
        Restaurant restaurant = new Restaurant("CROUS", "Valbonne");
        Schedule schedule = new Schedule(LocalTime.now().plusHours(1), LocalTime.now().plusHours(1).plusMinutes(30));
        restaurant.setSchedule(schedule);
        restaurant.getDeliverySchedule().generateDailySlots(LocalDate.now(), restaurant.getSchedule(), 5);

        cart.addDish(dish, 2, restaurant.getId());

        when(cartRepo.findActiveCartByUserId(user.getId())).thenReturn(java.util.Optional.of(cart));

        restaurant.open();
        TimeSlot slot = restaurant.getDeliverySchedule().getSlotsForDate(LocalDate.now()).getFirst();

        Order order = orderService.createOrder(user, restaurant, slot);
        assertEquals(OrderStatus.PENDING, order.getStatus());
        assertEquals(1, order.getOrderItems().size());
        verify(orderRepo, times(1)).save(order);

        restaurant.close();
    }

    @Test
    void testCreateOrderEmptyCartThrows() {
        User user = new User("u@mail.com", "User");
        Cart cart = new Cart(user.getId());

        when(cartRepo.findActiveCartByUserId(user.getId())).thenReturn(java.util.Optional.of(cart));

        Restaurant restaurant = new Restaurant("CROUS", "Valbonne");
        Schedule schedule = new Schedule(LocalTime.now().plusHours(1), LocalTime.now().plusHours(1).plusMinutes(30));
        restaurant.setSchedule(schedule);
        restaurant.getDeliverySchedule().generateDailySlots(LocalDate.now(), restaurant.getSchedule(), 5);
        restaurant.open();
        TimeSlot slot = restaurant.getDeliverySchedule().getSlotsForDate(LocalDate.now()).getFirst();

        assertThrows(InvalidCartOperationException.class, () -> orderService.createOrder(user, restaurant, slot));
        verify(orderRepo, never()).save(any());

        restaurant.close();
    }

    @Test
    void testPayOrderSuccessfully() {
        User user = new User("u@mail.com", "User");
        user.addCredit(BigDecimal.valueOf(100));
        Restaurant restaurant = new Restaurant("CROUS", "Valbonne");
        Schedule schedule = new Schedule(LocalTime.now().plusHours(1), LocalTime.now().plusHours(1).plusMinutes(30));
        restaurant.setSchedule(schedule);
        restaurant.getDeliverySchedule().generateDailySlots(LocalDate.now(), restaurant.getSchedule(), 5);
        restaurant.open();

        Cart cart = new Cart(user.getId());
        Dish dish = Dish.builder().name("Pizza").description("Delicious").price(BigDecimal.valueOf(20)).build();
        cart.addDish(dish, 2, restaurant.getId());

        when(cartRepo.findActiveCartByUserId(user.getId())).thenReturn(java.util.Optional.of(cart));

        TimeSlot slot = restaurant.getDeliverySchedule().getSlotsForDate(LocalDate.now()).getFirst();

        Order order = orderService.createOrder(user, restaurant, slot);
        orderService.payOrder(user, order);

        assertEquals(OrderStatus.PAID, order.getStatus());
        assertEquals(BigDecimal.valueOf(60), user.getStudentCredit());
        verify(orderRepo, times(2)).save(order);

        restaurant.close();
    }

    @Test
    void testPayOrderInsufficientCreditThrows() {
        User user = new User("u@mail.com", "User"); // 0 credit

        Restaurant restaurant = new Restaurant("CROUS", "Valbonne");
        Schedule schedule = new Schedule(LocalTime.now().plusHours(1), LocalTime.now().plusHours(1).plusMinutes(30));
        restaurant.setSchedule(schedule);
        restaurant.getDeliverySchedule().generateDailySlots(LocalDate.now(), restaurant.getSchedule(), 5);

        Cart cart = new Cart(user.getId());
        Dish dish = Dish.builder().name("Pizza").description("Delicious").price(BigDecimal.valueOf(20)).build();
        cart.addDish(dish, 2);

        when(cartRepo.findActiveCartByUserId(user.getId())).thenReturn(java.util.Optional.of(cart));

        restaurant.open();
        TimeSlot slot = restaurant.getDeliverySchedule().getSlotsForDate(LocalDate.now()).getFirst();

        var order = orderService.createOrder(user, restaurant, slot);

        assertThrows(InsufficientCreditException.class, () -> orderService.payOrder(user, order));
        assertEquals(OrderStatus.PENDING, order.getStatus());

        verify(orderRepo, times(1)).save(order);

        restaurant.close();
    }
}
