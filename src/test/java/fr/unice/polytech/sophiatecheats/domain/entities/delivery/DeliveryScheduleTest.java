package fr.unice.polytech.sophiatecheats.domain.entities.delivery;

import fr.unice.polytech.sophiatecheats.domain.entities.restaurant.TimeSlot;
import fr.unice.polytech.sophiatecheats.domain.entities.restaurant.Schedule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class DeliveryScheduleTest {

    private DeliverySchedule schedule;
    private Schedule restaurantSchedule;

    @BeforeEach
    void setUp() {
        UUID restaurantId = UUID.randomUUID();
        schedule = new DeliverySchedule(restaurantId);
        // Restaurant ouvert de 9h à 21h
        restaurantSchedule = new Schedule(LocalTime.of(9, 0), LocalTime.of(21, 0));
    }

    @Test
    void testGenerateDailySlotsWithSchedule() {
        LocalDate today = LocalDate.now();
        schedule.generateDailySlots(today, restaurantSchedule, 5);

        // De 9h à 21h = 12 heures = 24 créneaux de 30 minutes
        assertEquals(24, schedule.getSlotsForDate(today).size());

        // Vérifier que les créneaux respectent les heures d'ouverture
        var slots = schedule.getSlotsForDate(today);
        assertEquals(LocalTime.of(9, 0), slots.get(0).getStartTimeAsLocalTime());
        assertEquals(LocalTime.of(21, 0), slots.get(slots.size() - 1).getEndTimeAsLocalTime());
    }

    @Test
    void testGenerateWeeklySlots() {
        LocalDate startDate = LocalDate.now();
        schedule.generateWeeklySlots(startDate, restaurantSchedule, 3);

        // Vérifier que 7 jours ont été générés
        for (int i = 0; i < 7; i++) {
            LocalDate date = startDate.plusDays(i);
            assertFalse(schedule.getSlotsForDate(date).isEmpty());
            assertEquals(24, schedule.getSlotsForDate(date).size());
        }
    }

    @Test
    void testGetAvailableSlotsForDate() {
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        schedule.generateDailySlots(tomorrow, restaurantSchedule, 2);

        var availableSlots = schedule.getAvailableSlotsForDate(tomorrow);
        assertEquals(24, availableSlots.size()); // Tous disponibles initialement

        // Réserver un créneau
        TimeSlot firstSlot = availableSlots.get(0);
        schedule.reserveSlot(firstSlot.getId());

        // Vérifier que les créneaux disponibles ont diminué
        var updatedAvailableSlots = schedule.getAvailableSlotsForDate(tomorrow);
        assertEquals(24, updatedAvailableSlots.size()); // Le créneau est toujours disponible car capacité = 2
    }

    @Test
    void testReserveAndReleaseSlot() {
        // Utiliser une heure future pour que le slot soit considéré comme disponible
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        schedule.generateDailySlots(tomorrow, restaurantSchedule, 1);
        TimeSlot slot = schedule.getSlotsForDate(tomorrow).getFirst();
        assertTrue(slot.isAvailable());

        assertDoesNotThrow(() -> schedule.reserveSlot(slot.getId()));
        assertEquals(1, slot.getCurrentCapacity()); // Use correct method name
        assertFalse(slot.isAvailable());

        schedule.releaseSlot(slot.getId());
        assertEquals(0, slot.getCurrentCapacity()); // Use correct method name
        assertTrue(slot.isAvailable());
    }

    @Test
    void testReserveFullSlotThrowsException() {
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        schedule.generateDailySlots(tomorrow, restaurantSchedule, 1);
        TimeSlot slot = schedule.getSlotsForDate(tomorrow).get(0);

        // Remplir le créneau
        schedule.reserveSlot(slot.getId());

        // Tenter de réserver à nouveau doit lever une exception
        assertThrows(Exception.class, () -> schedule.reserveSlot(slot.getId()));
    }

    @Test
    void testCleanupPastSlots() {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        LocalDate tomorrow = LocalDate.now().plusDays(1);

        schedule.generateDailySlots(yesterday, restaurantSchedule, 5);
        schedule.generateDailySlots(tomorrow, restaurantSchedule, 5);

        // Avant cleanup
        assertFalse(schedule.getSlotsForDate(yesterday).isEmpty());
        assertFalse(schedule.getSlotsForDate(tomorrow).isEmpty());

        // Après cleanup
        schedule.cleanupPastSlots();
        assertTrue(schedule.getSlotsForDate(yesterday).isEmpty());
        assertFalse(schedule.getSlotsForDate(tomorrow).isEmpty());
    }

    @Test
    void testAvailableSlotCount() {
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        schedule.generateDailySlots(tomorrow, restaurantSchedule, 5);

        assertEquals(24, schedule.getAvailableSlotCount(tomorrow));

        // Réserver quelques créneaux jusqu'à ce qu'ils soient pleins
        var slots = schedule.getSlotsForDate(tomorrow);
        TimeSlot slot = slots.get(0);
        for (int i = 0; i < 5; i++) {
            slot.reserve();
        }

        // Le créneau est maintenant plein, donc le nombre disponible diminue
        assertEquals(23, schedule.getAvailableSlotCount(tomorrow));
    }

    @Test
    void testFindSlotByIdNotFound() {
        assertTrue(schedule.findSlotById(UUID.randomUUID()).isEmpty());
    }

    @Test
    void testLegacyMethodStillWorks() {
        // Test de la méthode @Deprecated pour compatibilité
        LocalDate today = LocalDate.now();
        schedule.generateDailySlots(today, LocalTime.of(12, 0), LocalTime.of(14, 0), 5);
        assertEquals(4, schedule.getSlotsForDate(today).size());
    }
}
