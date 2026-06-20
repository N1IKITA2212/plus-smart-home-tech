package ru.yandex.practicum.warehouse.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.interaction.api.WarehouseApi;
import ru.yandex.practicum.interaction.dto.*;
import ru.yandex.practicum.warehouse.service.WarehouseService;

@RestController
@RequiredArgsConstructor
public class WarehouseController implements WarehouseApi {

    private final WarehouseService service;

    @Override
    public void newProductInWarehouse(NewProductInWarehouseRequest request) {
        service.newProductInWarehouse(request);
    }

    @Override
    public BookedProductsDto checkProductQuantityEnoughForShoppingCart(ShoppingCartDto cart) {
        return service.checkProductQuantityEnoughForShoppingCart(cart);
    }

    @Override
    public void addProductToWarehouse(AddProductToWarehouseRequest request) {
        service.addProductToWarehouse(request);
    }

    @Override
    public AddressDto getWarehouseAddress() {
        return service.getWarehouseAddress();
    }
}
