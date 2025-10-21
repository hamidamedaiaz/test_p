package fr.unice.polytech.sophiatecheats.application.dto.order;

import fr.unice.polytech.sophiatecheats.domain.entities.order.Order;
import fr.unice.polytech.sophiatecheats.domain.entities.restaurant.Restaurant;
import fr.unice.polytech.sophiatecheats.domain.entities.restaurant.TimeSlot;
import fr.unice.polytech.sophiatecheats.domain.entities.user.User;
import fr.unice.polytech.sophiatecheats.domain.enums.PaymentMethod;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SelectDeliverySlotResponseTest {
    Restaurant restaurant;
    User user;
    Order order;
    TimeSlot timeSlot;
    SelectDeliverySlotResponse deliverySlotResponse;

    @BeforeEach
    void setUp() {
        restaurant = new Restaurant("restaurant","address");
        restaurant.getDeliverySchedule().generateDailySlots(LocalDate.now(),restaurant.getSchedule(),20);
        user = new User("user","password");
        order = new Order(user,restaurant,new ArrayList<>(), PaymentMethod.STUDENT_CREDIT);
        timeSlot = restaurant.getDeliverySchedule().getSlotsForDate(LocalDate.now()).getFirst();
    }

    @Test
    void should_validate_delivery_slot_response() {
        deliverySlotResponse = new SelectDeliverySlotResponse(order.getOrderId(), timeSlot.getId(),timeSlot.getStartTime(),timeSlot.getEndTime(),"message");

        assertTrue(deliverySlotResponse.isValid());
    }

    @Test
    void should_not_validate_delivery_slot_response() {
        deliverySlotResponse = new SelectDeliverySlotResponse(null, timeSlot.getId(),timeSlot.getStartTime(),timeSlot.getEndTime(),"message");

        assertFalse(deliverySlotResponse.isValid());

        deliverySlotResponse = new SelectDeliverySlotResponse("   ", timeSlot.getId(),timeSlot.getStartTime(),timeSlot.getEndTime(),"message");

        assertFalse(deliverySlotResponse.isValid());

        deliverySlotResponse = new SelectDeliverySlotResponse(order.getOrderId(), null,timeSlot.getStartTime(),timeSlot.getEndTime(),"message");

        assertFalse(deliverySlotResponse.isValid());

        deliverySlotResponse = new SelectDeliverySlotResponse(order.getOrderId(), timeSlot.getId(),null,timeSlot.getEndTime(),"message");

        assertFalse(deliverySlotResponse.isValid());

        deliverySlotResponse = new SelectDeliverySlotResponse(order.getOrderId(), timeSlot.getId(),timeSlot.getStartTime(),null,"message");

        assertFalse(deliverySlotResponse.isValid());
    }
}
