package fr.unice.polytech.sophiatecheats.cucumber.stepdefs;

import fr.unice.polytech.sophiatecheats.domain.entities.restaurant.TimeSlot;
import fr.unice.polytech.sophiatecheats.domain.exceptions.ValidationException;
import io.cucumber.java.Before;
import io.cucumber.java.en.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class DeliverySlotStepDefs {

    private final Map<String, TimeSlot> slots = new HashMap<>();
    private boolean selectionResult;
    private UUID selectedSlotId;
    private Exception caughtException;
    private TimeSlot selectedSlot;
    private String lastErrorMessage;

    @Before
    public void setup() {
        slots.clear();
        selectionResult = false;
        caughtException = null;
    }

    // Background Steps
    @Given("the campus restaurant {string} has generated delivery slots for today and tomorrow")
    public void the_restaurant_has_generated_slots(String restaurantName) {
        UUID restaurantId = UUID.randomUUID();
        LocalDate date = LocalDate.now().plusDays(1);

        for (int hour = 12; hour < 18; hour++) {
            for (int minute = 0; minute < 60; minute += 30) {
                LocalDateTime start = date.atTime(hour, minute);
                LocalDateTime end = start.plusMinutes(30);
                String label = String.format("%02d:%02d-%02d:%02d",
                        start.getHour(), start.getMinute(),
                        end.getHour(), end.getMinute());
                slots.put(label, new TimeSlot(restaurantId, start, end, 2));
            }
        }
    }

    @And("each slot has a maximum capacity of {int} orders")
    public void each_slot_has_max_capacity(Integer capacity) {
        slots.values().forEach(slot -> slot.setMaxCapacity(capacity));
    }

    
    // Scenario: User views available delivery slots
    @When("the user requests available delivery slots for today")
    public void user_requests_slots() {
        assertFalse(slots.isEmpty());
    }

    @Then("the system should display slots in 30-minute intervals")
    public void system_displays_30_minute_slots() {
        assertTrue(slots.values().stream().allMatch(s ->
                s.getEndTime().minusMinutes(30).equals(s.getStartTime())
        ));
    }

    @And("the system should mark past slots as unavailable")
    public void mark_past_slots_unavailable() {
        assertTrue(slots.values().stream()
                .filter(TimeSlot::isPast)
                .noneMatch(TimeSlot::isAvailable));
    }

    @And("the system should mark fully booked slots as unavailable")
    public void the_system_should_mark_fully_booked_slots_as_unavailable() {
        assertTrue(slots.values().stream()
                .filter(TimeSlot::isFull)
                .noneMatch(TimeSlot::isAvailable));
    }

    // Scenario: User selects a valid available delivery slot
    @Given("a delivery slot at {string} is available")
    public void a_slot_is_available(String label) {
        assertTrue(slots.containsKey(label));
        assertTrue(slots.get(label).isAvailable());
        selectedSlotId = slots.get(label).getId();
        selectedSlot = slots.get(label);
    }

    @When("the user selects this delivery slot")
    public void user_selects_slot() {
        attemptReservation(selectedSlot);
    }

    @Then("the slot should be reserved for the user")
    public void slot_should_be_reserved() {
        assertTrue(selectedSlot.getReservedCount() > 0);
    }

    @And("the system should confirm the selection")
    public void system_confirms_selection() {
        assertNull(caughtException);
    }

    // Scenario: User tries to select a fully booked slot
    @Given("a delivery slot at {string} is already fully booked")
    public void slot_fully_booked(String timeRange) {
        TimeSlot slot = findSlotByTime(timeRange);
        assertNotNull(slot, "Slot " + timeRange + " not found in generated slots");
        while (!slot.isFull()) {
            slot.reserve();
        }
        selectedSlot = slot;
        assertTrue(slot.isFull(), "Slot should be full after reservations");
    }

    @When("the user attempts to select this slot")
    public void user_attempts_select_full_slot() {
        attemptReservation(selectedSlot);
    }

    @Then("the system should display an error {string}")
    public void system_displays_error(String expectedMessage) {
        assertEquals(expectedMessage, lastErrorMessage);
    }

    @And("the selection should not be reserved")
    public void selection_should_not_be_reserved() {
        assertFalse(selectionResult);
    }

    // Scenario: Validate selected slot during order confirmation
    @Given("the user has selected a delivery slot at {string}")
    public void the_user_has_selected_a_delivery_slot_at(String label) {
        TimeSlot slot = findSlotByTime(label);
        assertNotNull(slot, "Slot " + label + " should exist");
        assertTrue(slot.isAvailable(), "Slot " + label + " should be available");
        slot.reserve();
        selectedSlotId = slot.getId();
        selectedSlot = slot;
        selectionResult = true;
    }

    @And("the slot is still available")
    public void the_slot_is_still_available() {
        assertTrue(slots.values().stream()
                .anyMatch(s -> s.getId().equals(selectedSlotId) && s.isAvailable()));
    }

    @When("the user confirms the order")
    public void the_user_confirms_the_order() {
        attemptReservation(selectedSlot);
    }

    @Then("the system should finalize the reservation")
    public void the_system_should_finalize_the_reservation() {
        assertTrue(selectionResult);
        assertNull(caughtException);
    }

    // Scenario: Validate slot that became unavailable
    @Given("the slot has become fully booked before order confirmation")
    public void the_slot_has_become_fully_booked_before_order_confirmation() {
        TimeSlot slot = slots.get(getLabelById(selectedSlotId));
        while (!slot.isFull()) slot.reserve();
    }

    // Scenario: User changes their selected slot before confirming
    @Given("the user selected slot {string}")
    public void the_user_selected_slot(String label) {
        selectedSlotId = slots.get(label).getId();
        selectedSlot = slots.get(label);
        slots.get(label).reserve();
    }

    @When("the user selects a different slot {string}")
    public void the_user_selects_a_different_slot(String newLabel) {
        TimeSlot oldSlot = slots.get(getLabelById(selectedSlotId));
        TimeSlot newSlot = findSlotByTime(newLabel);
        assertNotNull(newSlot, "New slot " + newLabel + " should exist");
        oldSlot.release();
        newSlot.reserve();
        selectedSlotId = newSlot.getId();
        selectedSlot = newSlot;
    }

    @Then("the system should reserve the new slot")
    public void the_system_should_reserve_the_new_slot() {
        assertTrue(slots.get(getLabelById(selectedSlotId)).isFull() ||
                slots.get(getLabelById(selectedSlotId)).getReservedCount() > 0);
    }

    @Then("release the previously selected slot")
    public void release_the_previously_selected_slot() {
        assertTrue(slots.values().stream().anyMatch(slot -> slot.getReservedCount() == 0));
    }

    // Scenario: Show estimated delivery time per slot
    @When("the user views available slots for today")
    public void the_user_views_available_slots_for_today() {
        assertFalse(slots.isEmpty());
    }

    @Then("each slot should display estimated preparation and delivery time")
    public void each_slot_should_display_estimated_preparation_and_delivery_time() {
        slots.values().forEach(slot -> {
            assertNotNull(slot.getDeliveryTime());
            assertTrue(slot.getDeliveryTime().isAfter(slot.getEndTime().toLocalTime()));
        });
    }

    // Helper methods
    private String getLabelById(UUID id) {
        return slots.entrySet().stream()
                .filter(e -> e.getValue().getId().equals(id))
                .map(Map.Entry::getKey)
                .findFirst().orElseThrow();
    }

    private TimeSlot findSlotByTime(String timeRange) {
        return slots.entrySet().stream()
                .filter(e -> e.getKey().equals(timeRange))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElse(null);
    }

    private void attemptReservation(TimeSlot slot) {
        try {
            slot.reserveOrThrow();
            lastErrorMessage = null; // réinitialise l'erreur précédente
        } catch (ValidationException e) {
            lastErrorMessage = e.getMessage();
        }
    }

}
