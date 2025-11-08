package fr.unice.polytech.sophiatecheats.application.dto.delivery;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

class TimeSlotDTOTest {

    UUID slot1Id = UUID.fromString("00000000-0000-0000-0000-000000000001");
    UUID slot2Id = UUID.fromString("00000000-0000-0000-0000-000000000002");
    UUID slot3Id = UUID.fromString("00000000-0000-0000-0000-000000000003");

    @Test
    void testValidDTO() {
        DeliverySlotDTO dto = new DeliverySlotDTO(
                slot1Id,
                UUID.randomUUID(),
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(30),
                5,
                0,
                true
        );
        assertTrue(dto.isValid());
    }

    @Test
    void testInvalidDTOMissingId() {
        DeliverySlotDTO dto = new DeliverySlotDTO(
                null,
                UUID.randomUUID(),
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(30),
                5,
                0,
                true
        );
        assertFalse(dto.isValid());
    }

    @Test
    void testInvalidDTOReservedExceedsMax() {
        DeliverySlotDTO dto = new DeliverySlotDTO(
                slot2Id,
                UUID.randomUUID(),
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(30),
                3,
                5,
                true
        );
        assertFalse(dto.isValid());
    }

    @Test
    void testInvalidDTOEndBeforeStart() {
        DeliverySlotDTO dto = new DeliverySlotDTO(
                slot3Id,
                UUID.randomUUID(),
                LocalDateTime.now().plusMinutes(30),
                LocalDateTime.now(),
                5,
                0,
                true
        );
        assertFalse(dto.isValid());
    }
}
