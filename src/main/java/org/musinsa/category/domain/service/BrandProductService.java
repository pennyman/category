package org.musinsa.category.domain.service;

import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.musinsa.category.api.dto.ApiResponseDto;
import org.musinsa.category.api.dto.BrandProductRequestDto;
import org.musinsa.category.domain.dto.ProductDto;
import org.musinsa.category.domain.entity.Brand;
import org.musinsa.category.domain.entity.Product;
import org.musinsa.category.domain.repository.BrandRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class BrandProductService {
    private final BrandRepository brandRepository;

    @Transactional
    public ApiResponseDto addBrandAndProducts(BrandProductRequestDto request) {
        try {
            Brand brand = new Brand();
            brand.setName(request.getBrand().getName());
            LocalDateTime now = LocalDateTime.now();
            brand.setCreatedDate(now);
            brand.setVersion(1L);

            request.getProducts().forEach(productDto -> {
                Product product = new Product();
                product.setCategory(productDto.getCategory());
                product.setPrice(productDto.getPrice());
                product.setCreatedDate(now);
                product.setVersion(1L);
                brand.addProduct(product);
            });

            Brand result= brandRepository.save(brand);
            System.out.println("result>>" + result);

            return new ApiResponseDto(true, "Brand and products added successfully");
        } catch (Exception e) {
            return new ApiResponseDto(false, "Failed to add brand and products: " + e.getMessage());
        }
    }

    @Transactional
    public ApiResponseDto updateBrandAndProducts(BrandProductRequestDto request) {
        try {
            Brand brand = brandRepository.findById(request.getBrand().getId())
                    .orElseThrow(() -> new EntityNotFoundException("Brand not found"));

            brand.setName(request.getBrand().getName());
            brand.setLastModifiedDate(LocalDateTime.now());
            brand.incrementVersion();

            Map<Long, ProductDto> productDtoMap = request.getProducts().stream()
                    .collect(Collectors.toMap(ProductDto::getId, Function.identity()));

            brand.getProducts().forEach(product -> {
                ProductDto productDto = productDtoMap.get(product.getId());
                if (productDto != null) {
                    product.setCategory(productDto.getCategory());
                    product.setPrice(productDto.getPrice());
                    product.setLastModifiedDate(LocalDateTime.now());
                    product.incrementVersion();
                    productDtoMap.remove(product.getId());
                }
            });

            productDtoMap.values().forEach(productDto -> {
                Product newProduct = new Product();
                newProduct.setCategory(productDto.getCategory());
                newProduct.setPrice(productDto.getPrice());
                newProduct.setLastModifiedDate(LocalDateTime.now());
                newProduct.setVersion(1L);
                brand.addProduct(newProduct);
            });

            return new ApiResponseDto(true, "Brand and products updated successfully");
        } catch (Exception e) {
            log.info("created={}", e.getMessage());
            return new ApiResponseDto(false, "Failed to update brand and products: " + e.getMessage());
        }
    }

    @Transactional
    public ApiResponseDto deleteBrandAndProducts(BrandProductRequestDto request) {
        try {
            Brand brand = brandRepository.findById(request.getBrand().getId())
                    .orElseThrow(() -> new EntityNotFoundException("Brand not found"));

            LocalDateTime now = LocalDateTime.now();
            deleteBrand(brand, now);

            brand.getProducts().forEach(product -> deleteProduct(product, now));

            return new ApiResponseDto(true, "Brand and products deleted successfully");
        } catch (Exception e) {
            return new ApiResponseDto(false, "Failed to delete brand and products: " + e.getMessage());
        }
    }

    private void deleteBrand(Brand brand, LocalDateTime deleteTime) {
        brand.setDeletedDate(deleteTime);
        brand.setLastModifiedDate(deleteTime);
        brand.incrementVersion();
    }

    private void deleteProduct(Product product, LocalDateTime deleteTime) {
        product.setDeletedDate(deleteTime);
        product.setLastModifiedDate(deleteTime);
        product.incrementVersion();
    }

}


