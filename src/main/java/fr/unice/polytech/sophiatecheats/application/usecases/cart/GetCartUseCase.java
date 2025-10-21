package fr.unice.polytech.sophiatecheats.application.usecases.cart;

import fr.unice.polytech.sophiatecheats.application.dto.user.CartDetailsResponse;
import fr.unice.polytech.sophiatecheats.application.dto.user.CartItemDto;
import fr.unice.polytech.sophiatecheats.application.usecases.UseCase;
import fr.unice.polytech.sophiatecheats.domain.entities.cart.Cart;
import fr.unice.polytech.sophiatecheats.domain.entities.cart.CartItem;
import fr.unice.polytech.sophiatecheats.domain.repositories.CartRepository;

import java.util.List;
import java.util.UUID;


public class GetCartUseCase implements UseCase<UUID, CartDetailsResponse> {

    private final CartRepository cartRepository;


    public GetCartUseCase(CartRepository cartRepository) {
        this.cartRepository = cartRepository;
    }


    @Override
    public CartDetailsResponse execute(UUID userId) {
        Cart cart = cartRepository.findActiveCartByUserId(userId)
            .orElse(createEmptyCart(userId));

        return mapCartToResponse(cart);
    }


    private Cart createEmptyCart(UUID userId) {
        return new Cart(userId);
    }


    private CartDetailsResponse mapCartToResponse(Cart cart) {
        List<CartItemDto> itemDtos = cart.getItems().stream()
            .map(this::mapCartItemToDto)
            .toList();

        return new CartDetailsResponse(
            cart.getId(),
            cart.getUserId(),
            itemDtos,
            cart.calculateTotal(),
            cart.getTotalItems(),
            cart.isEmpty()
        );
    }


    private CartItemDto mapCartItemToDto(CartItem item) {
        return new CartItemDto(
            item.getDishId(),
            item.getDishName(),
            item.getDishDescription(),
            item.getUnitPrice(),
            item.getQuantity(),
            item.getSubtotal()
        );
    }
}
