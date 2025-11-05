package fr.unice.polytech.sophiatecheats;

import fr.unice.polytech.sophiatecheats.application.dto.restaurant.BrowseRestaurantsRequest;
import fr.unice.polytech.sophiatecheats.application.dto.restaurant.BrowseRestaurantsResponse;
import fr.unice.polytech.sophiatecheats.application.usecases.restaurant.BrowseRestaurantsUseCase;
import fr.unice.polytech.sophiatecheats.domain.enums.DishCategory;
import fr.unice.polytech.sophiatecheats.infrastructure.config.ApplicationConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class US1DemoTest {

    private BrowseRestaurantsUseCase browseRestaurantsUseCase;

    @BeforeEach
    void setUp() {
        ApplicationConfig config = new ApplicationConfig();
        browseRestaurantsUseCase = config.getInstance(BrowseRestaurantsUseCase.class);
    }

    @Test
    void demonstrateUS1_BrowseAllRestaurants() {
        System.out.println("=== US1 Demo: Browse tous les restaurants ===");
        
        BrowseRestaurantsRequest request = new BrowseRestaurantsRequest(null, null);
        BrowseRestaurantsResponse response = browseRestaurantsUseCase.execute(request);
        
        System.out.println("Nombre de restaurants trouvés: " + response.restaurants().size());
        response.restaurants().forEach(restaurant -> {
            String status = restaurant.isOpen() ? "OUVERT" : "FERMÉ";
            System.out.println("- " + restaurant.name() + " (" + status + ")");
            restaurant.dishes().forEach(dish -> {
                System.out.println("  * " + dish.name() + " - " + dish.price() + "€ (" + dish.category() + ")");
            });
        });
        
        assertEquals(3, response.restaurants().size());
    }

    @Test
    void demonstrateUS1_BrowseOnlyOpenRestaurants() {
        System.out.println("\n=== US1 Demo: Browse seulement les restaurants ouverts ===");
        
        BrowseRestaurantsRequest request = new BrowseRestaurantsRequest(null, true);
        BrowseRestaurantsResponse response = browseRestaurantsUseCase.execute(request);
        
        System.out.println("Nombre de restaurants ouverts: " + response.restaurants().size());
        response.restaurants().forEach(restaurant -> {
            System.out.println("- " + restaurant.name() + " (OUVERT)");
        });
        
        assertTrue(response.restaurants().size() >= 1);
    }

    @Test
    void demonstrateUS1_BrowseVegetarianFood() {
        System.out.println("\n=== US1 Demo: Browse restaurants avec de la cuisine végétarienne ===");
        
        BrowseRestaurantsRequest request = new BrowseRestaurantsRequest(DishCategory.VEGETARIAN, null);
        BrowseRestaurantsResponse response = browseRestaurantsUseCase.execute(request);
        
        System.out.println("Restaurants avec cuisine végétarienne:");
        response.restaurants().forEach(restaurant -> {
            System.out.println("- " + restaurant.name());
            restaurant.dishes().stream()
                    .filter(dish -> DishCategory.VEGETARIAN.equals(dish.category()))
                    .forEach(dish -> System.out.println("  * " + dish.name()));
        });
        
        assertTrue(response.restaurants().size() >= 1);
    }
}