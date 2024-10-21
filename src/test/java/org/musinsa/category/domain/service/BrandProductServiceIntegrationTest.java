package org.musinsa.category.domain.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.musinsa.category.api.dto.ApiResponseDto;
import org.musinsa.category.api.dto.BrandProductRequestDto;
import org.musinsa.category.domain.dto.BrandDto;
import org.musinsa.category.domain.dto.ProductDto;
import org.musinsa.category.domain.entity.Brand;
import org.musinsa.category.domain.repository.BrandRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

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

    @Test
    @DisplayName("새로운 브랜드와 제품 추가 실패 테스트 - 중복된 브랜드 이름")
    void testAddNewBrandAndProductsFailureDuplicateBrandName() {
        // Given
        BrandProductRequestDto request1 = createTestRequest("Test Brand", "Category 1:10000");
        brandProductService.addBrandAndProducts(request1);

        BrandProductRequestDto request2 = createTestRequest("Test Brand", "Category 2:20000");

        // When
        ApiResponseDto response = brandProductService.addBrandAndProducts(request2);

        // Then
        assertFalse(response.isSuccess(), "중복된 브랜드 이름으로 인해 추가가 실패해야 합니다.");
        assertTrue(response.getMessage().contains("Failed to add brand and products"), "실패 메시지가 포함되어야 합니다.");
    }

    @Test
    @DisplayName("기존 브랜드와 제품 업데이트 실패 테스트 - 존재하지 않는 브랜드")
    void testUpdateNonExistingBrandAndProductsFailure() {
        // Given
        BrandDto nonExistingBrandDto = new BrandDto(9999L, "Non-existing Brand");
        ProductDto productDto = new ProductDto(null, "Category", 10000L, null);
        BrandProductRequestDto updateRequest = new BrandProductRequestDto(nonExistingBrandDto, Arrays.asList(productDto));

        // When
        ApiResponseDto response = brandProductService.updateBrandAndProducts(updateRequest);

        // Then
        assertFalse(response.isSuccess(), "존재하지 않는 브랜드 업데이트가 실패해야 합니다.");
        assertTrue(response.getMessage().contains("Failed to update brand and products"), "실패 메시지가 포함되어야 합니다.");
    }

    @Test
    @DisplayName("브랜드와 제품 삭제 실패 테스트 - 존재하지 않는 브랜드")
    void testDeleteNonExistingBrandAndProductsFailure() {
        // Given
        BrandProductRequestDto deleteRequest = new BrandProductRequestDto(new BrandDto(9999L, null), null);

        // When
        ApiResponseDto response = brandProductService.deleteBrandAndProducts(deleteRequest);

        // Then
        assertFalse(response.isSuccess(), "존재하지 않는 브랜드 삭제가 실패해야 합니다.");
        assertTrue(response.getMessage().contains("Failed to delete brand and products"), "실패 메시지가 포함되어야 합니다.");
    }

    @Test
    @DisplayName("새로운 브랜드와 제품 추가 실패 테스트 - 잘못된 제품 데이터")
    void testAddNewBrandAndProductsFailureInvalidProductData() {
        // Given
        BrandDto brandDto = new BrandDto(null, "Test Brand");
        ProductDto invalidProductDto = new ProductDto(null, "", -1000L, null);
        BrandProductRequestDto request = new BrandProductRequestDto(brandDto, List.of(invalidProductDto));

        // When
        ApiResponseDto response = brandProductService.addBrandAndProducts(request);

        // Then
        assertFalse(response.isSuccess(), "잘못된 제품 데이터로 인해 추가가 실패해야 합니다.");
        assertTrue(response.getMessage().contains("Failed to add brand and products"), "실패 메시지가 포함되어야 합니다.");
    }

    @Test
    @DisplayName("기존 브랜드와 제품 업데이트 실패 테스트 - 잘못된 제품 ID")
    void testUpdateExistingBrandAndProductsFailureInvalidProductId() {
        // Given
        BrandProductRequestDto addRequest = createTestRequest("Original Brand", "Category 1:10000");
        brandProductService.addBrandAndProducts(addRequest);

        Brand savedBrand = brandRepository.findAll().get(0);

        BrandDto updatedBrandDto = new BrandDto(savedBrand.getId(), "Updated Brand");
        ProductDto invalidProductDto = new ProductDto(9999L, "Invalid Category", 15000L, null);

        BrandProductRequestDto updateRequest = new BrandProductRequestDto(updatedBrandDto, List.of(invalidProductDto));

        // When
        ApiResponseDto response = brandProductService.updateBrandAndProducts(updateRequest);

        // Then
        assertFalse(response.isSuccess(), "존재하지 않는 제품 ID로 인해 업데이트가 실패해야 합니다.");
        assertTrue(response.getMessage().contains("Failed to update brand and products"), "실패 메시지가 포함되어야 합니다.");
    }

}