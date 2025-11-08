package fr.unice.polytech.sophiatecheats.application.facade;

import fr.unice.polytech.sophiatecheats.application.dto.order.request.ConfirmOrderRequest;
import fr.unice.polytech.sophiatecheats.application.dto.order.response.ConfirmOrderResponse;
import fr.unice.polytech.sophiatecheats.application.dto.restaurant.dishManagement.request.AddDishToRestaurantRequest;
import fr.unice.polytech.sophiatecheats.application.dto.restaurant.dishManagement.request.UpdateDishRequest;
import fr.unice.polytech.sophiatecheats.application.dto.restaurant.dishManagement.response.AddDishToRestaurantResponse;
import fr.unice.polytech.sophiatecheats.application.dto.restaurant.dishManagement.response.UpdateDishResponse;
import fr.unice.polytech.sophiatecheats.application.dto.user.request.AddDishToCartRequest;
import fr.unice.polytech.sophiatecheats.application.dto.user.request.BrowseRestaurantsRequest;
import fr.unice.polytech.sophiatecheats.application.dto.user.request.PlaceOrderRequest;
import fr.unice.polytech.sophiatecheats.application.dto.user.response.AddDishToCartResponse;
import fr.unice.polytech.sophiatecheats.application.dto.user.response.BrowseRestaurantsResponse;
import fr.unice.polytech.sophiatecheats.application.dto.user.response.PlaceOrderResponse;
import fr.unice.polytech.sophiatecheats.application.usecases.user.BrowseRestaurantsUseCase;
import fr.unice.polytech.sophiatecheats.application.usecases.user.order.PlaceOrderUseCase;
import fr.unice.polytech.sophiatecheats.application.usecases.cart.AddDishToCartUseCase;
import fr.unice.polytech.sophiatecheats.application.usecases.cart.ClearCartUseCase;
import fr.unice.polytech.sophiatecheats.application.usecases.cart.CancelCartUseCase;
import fr.unice.polytech.sophiatecheats.application.usecases.order.ConfirmOrderUseCase;
import fr.unice.polytech.sophiatecheats.application.usecases.restaurant.AddDishToRestaurantUseCase;
import fr.unice.polytech.sophiatecheats.application.usecases.restaurant.UpdateDishUseCase;
import fr.unice.polytech.sophiatecheats.infrastructure.config.ApplicationConfig;

public class SophiaTechEatsFacade {

    private final BrowseRestaurantsUseCase browseRestaurantsUseCase;
    private final AddDishToCartUseCase addDishToCartUseCase;
    private final ClearCartUseCase clearCartUseCase;
    private final CancelCartUseCase cancelCartUseCase;
    private final PlaceOrderUseCase placeOrderUseCase;
    private final ConfirmOrderUseCase confirmOrderUseCase;
    private final AddDishToRestaurantUseCase addDishToRestaurantUseCase;
    private final UpdateDishUseCase updateDishUseCase;

    public SophiaTechEatsFacade(ApplicationConfig config) {
        this.browseRestaurantsUseCase = config.getInstance(BrowseRestaurantsUseCase.class);
        this.addDishToCartUseCase = config.getInstance(AddDishToCartUseCase.class);
        this.clearCartUseCase = config.getInstance(ClearCartUseCase.class);
        this.cancelCartUseCase = config.getInstance(CancelCartUseCase.class);
        this.placeOrderUseCase = config.getInstance(PlaceOrderUseCase.class);
        this.confirmOrderUseCase = config.getInstance(ConfirmOrderUseCase.class);
        this.addDishToRestaurantUseCase = config.getInstance(AddDishToRestaurantUseCase.class);
        this.updateDishUseCase = config.getInstance(UpdateDishUseCase.class);
    }

    public BrowseRestaurantsResponse browseRestaurants(BrowseRestaurantsRequest request) {
        return browseRestaurantsUseCase.execute(request);
    }

    public AddDishToCartResponse addDishToCart(AddDishToCartRequest request) {
        return addDishToCartUseCase.execute(request);
    }

    public void clearCart(java.util.UUID userId) {
        clearCartUseCase.execute(userId);
    }

    public void cancelCart(java.util.UUID userId) {
        cancelCartUseCase.execute(userId);
    }

    public PlaceOrderResponse placeOrder(PlaceOrderRequest request) {
        return placeOrderUseCase.execute(request);
    }

    public ConfirmOrderResponse confirmOrder(ConfirmOrderRequest request) {
        return confirmOrderUseCase.execute(request);
    }

    public AddDishToRestaurantResponse addDishToRestaurant(AddDishToRestaurantRequest request) {
        return addDishToRestaurantUseCase.execute(request);
    }

    public UpdateDishResponse updateDish(UpdateDishRequest request) {
        return updateDishUseCase.execute(request);
    }

    public BrowseRestaurantsResponse browseAllRestaurants() {
        return browseRestaurantsUseCase.execute(
            new BrowseRestaurantsRequest(null, null, null, null, null, null)
        );
    }

    public BrowseRestaurantsResponse browseAvailableRestaurants() {
        return browseRestaurantsUseCase.execute(
            new BrowseRestaurantsRequest(null, true, null, null, null, null)
        );
    }
}

