package fr.unice.polytech.sophiatecheats.application.dto.user;

import fr.unice.polytech.sophiatecheats.application.dto.user.request.UpdateCartItemRequest;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UpdateCartItemRequestTest {
    @Test
    void should_create_object_correctly(){
        UUID userId = UUID.randomUUID();
        UUID dishId = UUID.randomUUID();
        UpdateCartItemRequest updateCartItemRequest = new UpdateCartItemRequest(userId,dishId,1);

        assertEquals(userId,updateCartItemRequest.userId());
        assertEquals(dishId,updateCartItemRequest.dishId());
        assertEquals(1,updateCartItemRequest.newQuantity());
    }

    @Test
    void should_throw_exception_when_arguments_are_invalid(){
        assertThrows(IllegalArgumentException.class, () -> new UpdateCartItemRequest(null,UUID.randomUUID(),1));
        assertThrows(IllegalArgumentException.class, () -> new UpdateCartItemRequest(UUID.randomUUID(),null,1));
    }
}
