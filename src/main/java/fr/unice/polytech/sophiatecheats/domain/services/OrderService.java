package fr.unice.polytech.sophiatecheats.domain.services;

import fr.unice.polytech.sophiatecheats.domain.entities.cart.Cart;
import fr.unice.polytech.sophiatecheats.domain.entities.order.Order;
import fr.unice.polytech.sophiatecheats.domain.entities.order.OrderItem;
import fr.unice.polytech.sophiatecheats.domain.entities.restaurant.Restaurant;
import fr.unice.polytech.sophiatecheats.domain.entities.restaurant.TimeSlot;
import fr.unice.polytech.sophiatecheats.domain.entities.user.User;
import fr.unice.polytech.sophiatecheats.domain.enums.OrderStatus;
import fr.unice.polytech.sophiatecheats.domain.enums.PaymentMethod;
import fr.unice.polytech.sophiatecheats.domain.exceptions.InsufficientCreditException;
import fr.unice.polytech.sophiatecheats.domain.exceptions.InvalidCartOperationException;
import fr.unice.polytech.sophiatecheats.domain.exceptions.OrderException;
import fr.unice.polytech.sophiatecheats.domain.repositories.OrderRepository;

import java.math.BigDecimal;
import java.util.List;

public class OrderService {

    private final OrderRepository orderRepository;
    private final CartService cartService;

    public OrderService(OrderRepository orderRepository, CartService cartService) {
        this.orderRepository = orderRepository;
        this.cartService = cartService;
    }

    public Order createOrder(User user, Restaurant restaurant, TimeSlot slot) {
        Cart cart = cartService.getCart(user);
        if (cart.isEmpty()) {
            throw new InvalidCartOperationException("Cannot create order with empty cart");
        }

        List<OrderItem> orderItems = cart.getItems().stream()
                .map(ci -> new OrderItem(ci.getDish(), ci.getQuantity()))
                .toList();

        Order order = new Order(user, restaurant, orderItems, PaymentMethod.STUDENT_CREDIT);
        order.setDeliveryTime(slot.getStartTime());

        restaurant.reserveDeliverySlot(slot.getId());

        orderRepository.save(order);
        return order;
    }

    public void payOrder(User user, Order order) {
        if (order.getStatus() != OrderStatus.PENDING && order.getStatus() != OrderStatus.CREATED) {
            throw new OrderException("Order cannot be paid unless status is PENDING or CREATED");
        }

        BigDecimal total = order.getTotalAmount();
        if (!user.hasEnoughCredit(total)) {
            throw new InsufficientCreditException("Insufficient credit");
        }

        user.deductCredit(total);
        order.setStatus(OrderStatus.PAID);
        orderRepository.save(order);

        cartService.clearCart(user);
    }
}
