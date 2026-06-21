package ru.yandex.practicum.store.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.interaction.dto.*;
import ru.yandex.practicum.store.exception.ProductNotFoundException;
import ru.yandex.practicum.store.model.Product;
import ru.yandex.practicum.store.repository.ProductRepository;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ShoppingStoreService {

    private final ProductRepository productRepository;

    @Transactional(readOnly = true)
    public Page<ProductDto> getProducts(ProductCategory category, int page, int size, List<String> sort) {
        Sort sorting = sort.isEmpty() ? Sort.unsorted() :
                Sort.by(sort.stream().map(Sort.Order::by).toList());
        PageRequest pageable = PageRequest.of(page, size, sorting);
        return productRepository.findByProductCategory(category, pageable)
                .map(this::toDto);
    }

    @Transactional
    public ProductDto createNewProduct(ProductDto dto) {
        Product product = fromDto(dto);
        product.setProductState(ProductState.ACTIVE);
        return toDto(productRepository.save(product));
    }

    @Transactional
    public ProductDto updateProduct(ProductDto dto) {
        Product existing = productRepository.findById(dto.getProductId())
                .orElseThrow(() -> new ProductNotFoundException("Product not found: " + dto.getProductId()));
        existing.setProductName(dto.getProductName());
        existing.setDescription(dto.getDescription());
        existing.setImageSrc(dto.getImageSrc());
        existing.setQuantityState(dto.getQuantityState());
        existing.setProductState(dto.getProductState());
        existing.setProductCategory(dto.getProductCategory());
        existing.setPrice(dto.getPrice());
        return toDto(productRepository.save(existing));
    }

    @Transactional
    public boolean removeProductFromStore(UUID productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product not found: " + productId));
        product.setProductState(ProductState.DEACTIVATE);
        productRepository.save(product);
        return true;
    }

    @Transactional
    public boolean setProductQuantityState(SetProductQuantityStateRequest request) {
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ProductNotFoundException("Product not found: " + request.getProductId()));
        product.setQuantityState(request.getQuantityState());
        productRepository.save(product);
        return true;
    }

    @Transactional(readOnly = true)
    public ProductDto getProduct(UUID productId) {
        return productRepository.findById(productId)
                .map(this::toDto)
                .orElseThrow(() -> new ProductNotFoundException("Product not found: " + productId));
    }

    private ProductDto toDto(Product product) {
        return ProductDto.builder()
                .productId(product.getProductId())
                .productName(product.getProductName())
                .description(product.getDescription())
                .imageSrc(product.getImageSrc())
                .quantityState(product.getQuantityState())
                .productState(product.getProductState())
                .productCategory(product.getProductCategory())
                .price(product.getPrice())
                .build();
    }

    private Product fromDto(ProductDto dto) {
        return Product.builder()
                .productName(dto.getProductName())
                .description(dto.getDescription())
                .imageSrc(dto.getImageSrc())
                .quantityState(dto.getQuantityState())
                .productState(dto.getProductState())
                .productCategory(dto.getProductCategory())
                .price(dto.getPrice())
                .build();
    }
}
