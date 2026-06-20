package ru.yandex.practicum.cart.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.cart.exception.NoProductsInShoppingCartException;
import ru.yandex.practicum.cart.exception.NotAuthorizedUserException;
import ru.yandex.practicum.cart.model.ShoppingCart;
import ru.yandex.practicum.cart.repository.ShoppingCartRepository;
import ru.yandex.practicum.interaction.dto.ChangeProductQuantityRequest;
import ru.yandex.practicum.interaction.dto.ShoppingCartDto;
import ru.yandex.practicum.interaction.feign.WarehouseClient;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ShoppingCartService {

    private final ShoppingCartRepository cartRepository;
    private final WarehouseClient warehouseClient;

    @Transactional
    public ShoppingCartDto getShoppingCart(String username) {
        validateUsername(username);
        ShoppingCart cart = cartRepository.findByUsernameAndActiveTrue(username)
                .orElseGet(() -> cartRepository.save(
                        ShoppingCart.builder().username(username).build()
                ));
        return toDto(cart);
    }

    @Transactional
    public ShoppingCartDto addProductToShoppingCart(String username, Map<UUID, Long> products) {
        validateUsername(username);
        ShoppingCart cart = cartRepository.findByUsernameAndActiveTrue(username)
                .orElseGet(() -> cartRepository.save(
                        ShoppingCart.builder().username(username).build()
                ));
        products.forEach((id, qty) ->
                cart.getProducts().merge(id, qty, Long::sum)
        );
        warehouseClient.checkProductQuantityEnoughForShoppingCart(toDto(cart));
        return toDto(cartRepository.save(cart));
    }

    @Transactional
    public void deactivateCurrentShoppingCart(String username) {
        validateUsername(username);
        cartRepository.findByUsernameAndActiveTrue(username).ifPresent(cart -> {
            cart.setActive(false);
            cartRepository.save(cart);
        });
    }

    @Transactional
    public ShoppingCartDto removeFromShoppingCart(String username, List<UUID> productIds) {
        validateUsername(username);
        ShoppingCart cart = cartRepository.findByUsernameAndActiveTrue(username)
                .orElseThrow(() -> new NoProductsInShoppingCartException("No active cart for user: " + username));
        for (UUID id : productIds) {
            if (!cart.getProducts().containsKey(id)) {
                throw new NoProductsInShoppingCartException("Product not in cart: " + id);
            }
            cart.getProducts().remove(id);
        }
        return toDto(cartRepository.save(cart));
    }

    @Transactional
    public ShoppingCartDto changeProductQuantity(String username, ChangeProductQuantityRequest request) {
        validateUsername(username);
        ShoppingCart cart = cartRepository.findByUsernameAndActiveTrue(username)
                .orElseThrow(() -> new NoProductsInShoppingCartException("No active cart for user: " + username));
        if (!cart.getProducts().containsKey(request.getProductId())) {
            throw new NoProductsInShoppingCartException("Product not in cart: " + request.getProductId());
        }
        cart.getProducts().put(request.getProductId(), request.getNewQuantity());
        return toDto(cartRepository.save(cart));
    }

    private void validateUsername(String username) {
        if (username == null || username.isBlank()) {
            throw new NotAuthorizedUserException("Username must not be empty");
        }
    }

    private ShoppingCartDto toDto(ShoppingCart cart) {
        return ShoppingCartDto.builder()
                .shoppingCartId(cart.getShoppingCartId())
                .products(cart.getProducts())
                .build();
    }
}
