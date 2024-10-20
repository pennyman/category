package org.musinsa.category.domain.service;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.musinsa.category.api.dto.ApiResponseDto;
import org.musinsa.category.api.dto.BrandProductRequestDto;
import org.musinsa.category.domain.dto.BrandDto;
import org.musinsa.category.domain.dto.ProductDto;
import org.musinsa.category.domain.entity.Brand;
import org.musinsa.category.domain.entity.Product;
import org.musinsa.category.domain.repository.BrandRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@SpringBootTest
@Transactional
class BrandProductServiceIntegrationTest {

    @Autowired
    private BrandProductService brandProductService;

    @Autowired
    private BrandRepository brandRepository;

    private BrandProductRequestDto createTestRequest(String brandName, String... productDetails) {
        BrandDto brandDto = new BrandDto();
        brandDto.setName(brandName);

        List<ProductDto> products = Arrays.stream(productDetails)
                .map(detail -> {
                    String[] parts = detail.split(":");
                    ProductDto product = new ProductDto();
                    product.setCategory(parts[0]);
                    product.setPrice(Long.parseLong(parts[1]));
                    return product;
                })
                .toList();

        return new BrandProductRequestDto(brandDto, products);
    }

    @Test
    @DisplayName("새로운 브랜드와 제품 추가 성공 테스트")
    void testAddNewBrandAndProductsSuccessfully() {
        // Given
        BrandProductRequestDto request = createTestRequest("Test Brand", "Category 1:10000", "Category 2:20000");

        // When
        ApiResponseDto response = brandProductService.addBrandAndProducts(request);

        // Then
        assertTrue(response.isSuccess(), "브랜드와 제품 추가가 성공해야 합니다.");
        assertEquals("Brand and products added successfully", response.getMessage(), "성공 메시지가 일치해야 합니다.");

        List<Brand> brands = brandRepository.findAll();
        assertEquals(1, brands.size(), "하나의 브랜드만 저장되어야 합니다.");

        Brand savedBrand = brands.get(0);
        assertEquals("Test Brand", savedBrand.getName(), "저장된 브랜드 이름이 일치해야 합니다.");
        assertEquals(2, savedBrand.getProducts().size(), "두 개의 제품이 저장되어야 합니다.");
    }

    @Test
    @DisplayName("기존 브랜드와 제품 업데이트 성공 테스트")
    void testUpdateExistingBrandAndProductsSuccessfully() {
        // Given
        BrandProductRequestDto addRequest = createTestRequest("Original Brand", "Category 1:10000", "Category 2:20000");
        brandProductService.addBrandAndProducts(addRequest);

        Brand savedBrand = brandRepository.findAll().get(0);

        BrandDto updatedBrandDto = new BrandDto(savedBrand.getId(), "Updated Brand");
        ProductDto updatedProduct = new ProductDto(savedBrand.getProducts().get(0).getId(), "Updated Category", 15000L, null);
        ProductDto newProduct = new ProductDto(null, "New Category", 30000L, null);

        BrandProductRequestDto updateRequest = new BrandProductRequestDto(updatedBrandDto, Arrays.asList(updatedProduct, newProduct));

        // When
        ApiResponseDto response = brandProductService.updateBrandAndProducts(updateRequest);

        // Then
        assertTrue(response.isSuccess(), "브랜드와 제품 업데이트가 성공해야 합니다.");
        assertEquals("Brand and products updated successfully", response.getMessage(), "성공 메시지가 일치해야 합니다.");

        Brand updatedBrand = brandRepository.findById(savedBrand.getId()).orElseThrow();
        assertEquals("Updated Brand", updatedBrand.getName(), "브랜드 이름이 업데이트되어야 합니다.");
        assertEquals(3, updatedBrand.getProducts().size(), "세 개의 제품이 있어야 합니다.");
    }

    @Test
    @DisplayName("브랜드와 제품 삭제 성공 테스트")
    void testDeleteBrandAndProductsSuccessfully() {
        // Given
        BrandProductRequestDto addRequest = createTestRequest("Brand to Delete", "Category 1:10000", "Category 2:20000");
        brandProductService.addBrandAndProducts(addRequest);

        Brand savedBrand = brandRepository.findAll().get(0);

        BrandProductRequestDto deleteRequest = new BrandProductRequestDto(new BrandDto(savedBrand.getId(), null), null);

        // When
        ApiResponseDto response = brandProductService.deleteBrandAndProducts(deleteRequest);

        // Then
        assertTrue(response.isSuccess(), "브랜드와 제품 삭제가 성공해야 합니다.");
        assertEquals("Brand and products deleted successfully", response.getMessage(), "성공 메시지가 일치해야 합니다.");

        Brand deletedBrand = brandRepository.findById(savedBrand.getId()).orElseThrow();
        assertNotNull(deletedBrand.getDeletedDate(), "브랜드의 삭제 일자가 설정되어야 합니다.");
        assertTrue(deletedBrand.getProducts().stream().allMatch(p -> p.getDeletedDate() != null), "모든 제품의 삭제 일자가 설정되어야 합니다.");
    }
}