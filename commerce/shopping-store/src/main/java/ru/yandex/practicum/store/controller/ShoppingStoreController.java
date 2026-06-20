package ru.yandex.practicum.store.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.interaction.api.ShoppingStoreApi;
import ru.yandex.practicum.interaction.dto.*;
import ru.yandex.practicum.store.service.ShoppingStoreService;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class ShoppingStoreController implements ShoppingStoreApi {

    private final ShoppingStoreService service;

    @Override
    public Page<ProductDto> getProducts(ProductCategory category, int page, int size, List<String> sort) {
        return service.getProducts(category, page, size, sort);
    }

    @Override
    public ProductDto createNewProduct(ProductDto product) {
        return service.createNewProduct(product);
    }

    @Override
    public ProductDto updateProduct(ProductDto product) {
        return service.updateProduct(product);
    }

    @Override
    public boolean removeProductFromStore(UUID productId) {
        return service.removeProductFromStore(productId);
    }

    @Override
    public boolean setProductQuantityState(SetProductQuantityStateRequest request) {
        return service.setProductQuantityState(request);
    }

    @Override
    public ProductDto getProduct(UUID productId) {
        return service.getProduct(productId);
    }
}
