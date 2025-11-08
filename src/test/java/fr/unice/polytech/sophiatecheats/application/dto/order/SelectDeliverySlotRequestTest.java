package fr.unice.polytech.sophiatecheats.application.dto.order;

import fr.unice.polytech.sophiatecheats.application.dto.order.request.SelectDeliverySlotRequest;
import fr.unice.polytech.sophiatecheats.domain.entities.order.Order;
import fr.unice.polytech.sophiatecheats.domain.entities.restaurant.Restaurant;
import fr.unice.polytech.sophiatecheats.domain.entities.user.User;
import fr.unice.polytech.sophiatecheats.domain.enums.PaymentMethod;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SelectDeliverySlotRequestTest {
    Restaurant restaurant;
    User user;
    Order order;
    SelectDeliverySlotRequest deliverySlotRequest;

    @BeforeEach
    void setUp() {
        restaurant = new Restaurant("restaurant","address");
        restaurant.getDeliverySchedule().generateDailySlots(LocalDate.now(),restaurant.getSchedule(),20);
        user = new User("user","password");
        order = new Order(user,restaurant,new ArrayList<>(), PaymentMethod.STUDENT_CREDIT);
    }

    @Test
    void should_validate_delivery_slot_request() {
        deliverySlotRequest = new SelectDeliverySlotRequest(order.getOrderId(), restaurant.getDeliverySchedule().getSlotsForDate(LocalDate.now()).getFirst().getId());

        assertTrue(deliverySlotRequest.isValid());
    }

    @Test
    void should_not_validate_delivery_slot_request() {
        deliverySlotRequest = new SelectDeliverySlotRequest(null, restaurant.getDeliverySchedule().getSlotsForDate(LocalDate.now()).getFirst().getId());

        assertFalse(deliverySlotRequest.isValid());

        deliverySlotRequest = new SelectDeliverySlotRequest("   ", restaurant.getDeliverySchedule().getSlotsForDate(LocalDate.now()).getFirst().getId());

        assertFalse(deliverySlotRequest.isValid());

        deliverySlotRequest = new SelectDeliverySlotRequest(order.getOrderId(),null);

        assertFalse(deliverySlotRequest.isValid());
    }
}
