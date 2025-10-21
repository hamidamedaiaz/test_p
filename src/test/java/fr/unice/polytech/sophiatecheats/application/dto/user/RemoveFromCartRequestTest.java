package fr.unice.polytech.sophiatecheats.application.dto.user;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RemoveFromCartRequestTest {

    @Test
    void should_create_object_correctly() {
        UUID userId = UUID.randomUUID();
        UUID dishId = UUID.randomUUID();
        RemoveFromCartRequest removeFromCartRequest = new RemoveFromCartRequest(userId, dishId);

        assertEquals(userId,removeFromCartRequest.userId());
        assertEquals(dishId,removeFromCartRequest.dishId());
    }

    @Test
    void should_throw_exception_when_arguments_are_invalid(){
        assertThrows(IllegalArgumentException.class, () -> new RemoveFromCartRequest(null, UUID.randomUUID()));
        assertThrows(IllegalArgumentException.class, () -> new RemoveFromCartRequest(UUID.randomUUID(), null));
    }
}
