package fr.unice.polytech.sophiatecheats.application.usecases.restaurant;

import fr.unice.polytech.sophiatecheats.application.dto.restaurant.dishManagement.request.AddDishToRestaurantRequest;
import fr.unice.polytech.sophiatecheats.application.dto.restaurant.dishManagement.request.RemoveDishFromRestaurantRequest;
import fr.unice.polytech.sophiatecheats.application.dto.restaurant.dishManagement.request.UpdateDishRequest;
import fr.unice.polytech.sophiatecheats.application.dto.restaurant.dishManagement.response.AddDishToRestaurantResponse;
import fr.unice.polytech.sophiatecheats.application.dto.restaurant.dishManagement.response.RemoveDishFromRestaurantResponse;
import fr.unice.polytech.sophiatecheats.application.dto.restaurant.dishManagement.response.UpdateDishResponse;
import fr.unice.polytech.sophiatecheats.domain.entities.restaurant.Dish;
import fr.unice.polytech.sophiatecheats.domain.entities.restaurant.Restaurant;
import fr.unice.polytech.sophiatecheats.domain.enums.DishCategory;
import fr.unice.polytech.sophiatecheats.domain.repositories.RestaurantRepository;
import fr.unice.polytech.sophiatecheats.infrastructure.repositories.memory.InMemoryRestaurantRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests pour les use cases de gestion des plats dans un restaurant.
 * Couvre l'ajout, la modification et la suppression de plats.
 */
class DishManagementUseCasesTest {

    private RestaurantRepository restaurantRepository;
    private AddDishToRestaurantUseCase addDishUseCase;
    private UpdateDishUseCase updateDishUseCase;
    private RemoveDishFromRestaurantUseCase removeDishUseCase;

    private UUID restaurantId;
    private Restaurant testRestaurant;

    @BeforeEach
    void setUp() {
        restaurantRepository = new InMemoryRestaurantRepository();
        addDishUseCase = new AddDishToRestaurantUseCase(restaurantRepository);
        updateDishUseCase = new UpdateDishUseCase(restaurantRepository);
        removeDishUseCase = new RemoveDishFromRestaurantUseCase(restaurantRepository);

        // Création d'un restaurant de test
        testRestaurant = new Restaurant("Test Restaurant", "123 Test Street");
        restaurantId = testRestaurant.getId();
        restaurantRepository.save(testRestaurant);
    }

    @Nested
    @DisplayName("Add Dish Use Case")
    class AddDishUseCaseTest {

        @Test
        @DisplayName("Should add dish successfully when valid request")
        void shouldAddDishSuccessfully() {
            // Given
            AddDishToRestaurantRequest request = new AddDishToRestaurantRequest(
                restaurantId,
                "Pizza Margherita",
                "Délicieuse pizza avec tomate et mozzarella",
                new BigDecimal("12.50"),
                DishCategory.MAIN_COURSE,
                true
            );

            // When
            AddDishToRestaurantResponse response = addDishUseCase.execute(request);

            // Then
            assertTrue(response.success());
            assertNotNull(response.dishId());
            assertTrue(response.message().contains("Pizza Margherita"));
            assertTrue(response.message().contains("ajouté avec succès"));
        }

        @Test
        @DisplayName("Should fail when restaurant not found")
        void shouldFailWhenRestaurantNotFound() {
            // Given
            UUID nonExistentRestaurantId = UUID.randomUUID();
            AddDishToRestaurantRequest request = new AddDishToRestaurantRequest(
                nonExistentRestaurantId,
                "Pizza",
                "Description",
                new BigDecimal("10.00"),
                DishCategory.MAIN_COURSE,
                true
            );

            // When
            AddDishToRestaurantResponse response = addDishUseCase.execute(request);

            // Then
            assertFalse(response.success());
            assertNull(response.dishId());
            assertTrue(response.message().contains("Restaurant non trouvé"));
        }

        @Test
        @DisplayName("Should throw exception when request is null")
        void shouldThrowExceptionWhenRequestIsNull() {
            // When & Then
            assertThrows(IllegalArgumentException.class, () -> addDishUseCase.execute(null));
        }
    }

    @Nested
    @DisplayName("Update Dish Use Case")
    class UpdateDishUseCaseTest {

        private UUID dishId;

        @BeforeEach
        void setUpDish() {
            // Ajouter un plat de test
            Dish testDish = Dish.builder()
                .name("Original Pizza")
                .description("Original description")
                .price(new BigDecimal("10.00"))
                .category(DishCategory.MAIN_COURSE)
                .available(true)
                .build();
            testRestaurant.addDish(testDish);
            dishId = testDish.getId();
            restaurantRepository.save(testRestaurant);
        }

        @Test
        @DisplayName("Should update dish name successfully")
        void shouldUpdateDishNameSuccessfully() {
            // Given
            UpdateDishRequest request = new UpdateDishRequest(
                restaurantId,
                dishId,
                "Updated Pizza Name",
                null,
                null,
                null,
                null
            );

            // When
            UpdateDishResponse response = updateDishUseCase.execute(request);

            // Then
            assertTrue(response.success());
            assertTrue(response.message().contains("Updated Pizza Name"));
            assertTrue(response.message().contains("modifié avec succès"));
        }

        @Test
        @DisplayName("Should update multiple properties")
        void shouldUpdateMultipleProperties() {
            // Given
            UpdateDishRequest request = new UpdateDishRequest(
                restaurantId,
                dishId,
                "New Name",
                "New Description",
                new BigDecimal("15.00"),
                DishCategory.DESSERT,
                false
            );

            // When
            UpdateDishResponse response = updateDishUseCase.execute(request);

            // Then
            assertTrue(response.success());
            assertTrue(response.message().contains("New Name"));
        }

        @Test
        @DisplayName("Should fail when dish not found")
        void shouldFailWhenDishNotFound() {
            // Given
            UUID nonExistentDishId = UUID.randomUUID();
            UpdateDishRequest request = new UpdateDishRequest(
                restaurantId,
                nonExistentDishId,
                "New Name",
                null,
                null,
                null,
                null
            );

            // When
            UpdateDishResponse response = updateDishUseCase.execute(request);

            // Then
            assertFalse(response.success());
            assertTrue(response.message().contains("Plat non trouvé"));
        }

        @Test
        @DisplayName("Should fail when restaurant not found")
        void shouldFailWhenRestaurantNotFound() {
            // Given
            UUID nonExistentRestaurantId = UUID.randomUUID();
            UpdateDishRequest request = new UpdateDishRequest(
                nonExistentRestaurantId,
                dishId,
                "New Name",
                null,
                null,
                null,
                null
            );

            // When
            UpdateDishResponse response = updateDishUseCase.execute(request);

            // Then
            assertFalse(response.success());
            assertTrue(response.message().contains("Restaurant non trouvé"));
        }
    }

    @Nested
    @DisplayName("Remove Dish Use Case")
    class RemoveDishUseCaseTest {

        private UUID dishId;
        private String dishName;

        @BeforeEach
        void setUpDish() {
            // Ajouter un plat de test
            Dish testDish = Dish.builder()
                .name("Pizza to Remove")
                .description("Description")
                .price(new BigDecimal("10.00"))
                .category(DishCategory.MAIN_COURSE)
                .available(true)
                .build();
            testRestaurant.addDish(testDish);
            dishId = testDish.getId();
            dishName = testDish.getName();
            restaurantRepository.save(testRestaurant);
        }

        @Test
        @DisplayName("Should remove dish successfully")
        void shouldRemoveDishSuccessfully() {
            // Given
            RemoveDishFromRestaurantRequest request = new RemoveDishFromRestaurantRequest(
                restaurantId,
                dishId
            );

            // When
            RemoveDishFromRestaurantResponse response = removeDishUseCase.execute(request);

            // Then
            assertTrue(response.success());
            assertTrue(response.message().contains(dishName));
            assertTrue(response.message().contains("supprimé avec succès"));
        }

        @Test
        @DisplayName("Should fail when dish not found")
        void shouldFailWhenDishNotFound() {
            // Given
            UUID nonExistentDishId = UUID.randomUUID();
            RemoveDishFromRestaurantRequest request = new RemoveDishFromRestaurantRequest(
                restaurantId,
                nonExistentDishId
            );

            // When
            RemoveDishFromRestaurantResponse response = removeDishUseCase.execute(request);

            // Then
            assertFalse(response.success());
            assertTrue(response.message().contains("Plat non trouvé"));
        }

        @Test
        @DisplayName("Should fail when restaurant not found")
        void shouldFailWhenRestaurantNotFound() {
            // Given
            UUID nonExistentRestaurantId = UUID.randomUUID();
            RemoveDishFromRestaurantRequest request = new RemoveDishFromRestaurantRequest(
                nonExistentRestaurantId,
                dishId
            );

            // When
            RemoveDishFromRestaurantResponse response = removeDishUseCase.execute(request);

            // Then
            assertFalse(response.success());
            assertTrue(response.message().contains("Restaurant non trouvé"));
        }

        @Test
        @DisplayName("Should throw exception when request is null")
        void shouldThrowExceptionWhenRequestIsNull() {
            // When & Then
            assertThrows(IllegalArgumentException.class, () -> removeDishUseCase.execute(null));
        }
    }
}
