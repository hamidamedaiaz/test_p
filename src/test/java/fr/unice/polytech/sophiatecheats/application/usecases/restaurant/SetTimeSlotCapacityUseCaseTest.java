package fr.unice.polytech.sophiatecheats.application.usecases.restaurant;

import fr.unice.polytech.sophiatecheats.domain.entities.restaurant.Restaurant;
import fr.unice.polytech.sophiatecheats.domain.entities.restaurant.TimeSlot;
import fr.unice.polytech.sophiatecheats.domain.exceptions.EntityNotFoundException;
import fr.unice.polytech.sophiatecheats.domain.exceptions.SlotNotFoundException;
import fr.unice.polytech.sophiatecheats.domain.exceptions.ValidationException;
import fr.unice.polytech.sophiatecheats.domain.repositories.RestaurantRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("SetTimeSlotCapacityUseCase - Tests unitaires")
class SetTimeSlotCapacityUseCaseTest {
    private RestaurantRepository restaurantRepository;
    private SetTimeSlotCapacityUseCase useCase;
    private UUID restaurantId;
    private UUID slotId;
    private Restaurant restaurant;
    private TimeSlot slot;

    @BeforeEach
    void setUp() {
        restaurantRepository = mock(RestaurantRepository.class);
        useCase = new SetTimeSlotCapacityUseCase(restaurantRepository);
        restaurantId = UUID.randomUUID();
        slotId = UUID.randomUUID();
        slot = TimeSlot.builder()
                .id(slotId)
                .restaurantId(restaurantId)
                .startTime(LocalDateTime.now().plusHours(1))
                .endTime(LocalDateTime.now().plusHours(2))
                .maxCapacity(10)
                .reservedCount(3)
                .build();
        restaurant = mock(Restaurant.class);
        when(restaurant.getDeliverySchedule()).thenReturn(mock(fr.unice.polytech.sophiatecheats.domain.entities.delivery.DeliverySchedule.class));
        when(restaurant.getDeliverySchedule().findSlotById(slotId)).thenReturn(Optional.of(slot));
    }

    @Test
    @DisplayName("Modifie la capacité max si nouvelle valeur valide")
    void setCapacitySuccess() {
        when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.of(restaurant));
        SetTimeSlotCapacityUseCase.Request req = new SetTimeSlotCapacityUseCase.Request(restaurantId, slotId, 8);
        useCase.execute(req);
        assertEquals(8, slot.getMaxCapacity());
        verify(restaurantRepository).save(restaurant);
    }

    @Test
    @DisplayName("Refuse si nouvelle capacité < réservations existantes")
    void setCapacityTooLowThrows() {
        when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.of(restaurant));
        SetTimeSlotCapacityUseCase.Request req = new SetTimeSlotCapacityUseCase.Request(restaurantId, slotId, 2);
        assertThrows(ValidationException.class, () -> useCase.execute(req));
        assertEquals(10, slot.getMaxCapacity()); // inchangé
        verify(restaurantRepository, never()).save(restaurant);
    }

    @Test
    @DisplayName("Refuse si créneau non trouvé")
    void slotNotFoundThrows() {
        when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.of(restaurant));
        when(restaurant.getDeliverySchedule().findSlotById(slotId)).thenReturn(Optional.empty());
        SetTimeSlotCapacityUseCase.Request req = new SetTimeSlotCapacityUseCase.Request(restaurantId, slotId, 8);
        assertThrows(SlotNotFoundException.class, () -> useCase.execute(req));
        verify(restaurantRepository, never()).save(restaurant);
    }

    @Test
    @DisplayName("Refuse si restaurant non trouvé")
    void restaurantNotFoundThrows() {
        when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.empty());
        SetTimeSlotCapacityUseCase.Request req = new SetTimeSlotCapacityUseCase.Request(restaurantId, slotId, 8);
        assertThrows(EntityNotFoundException.class, () -> useCase.execute(req));
    }
}

