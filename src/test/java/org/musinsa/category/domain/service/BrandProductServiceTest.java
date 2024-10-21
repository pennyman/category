package org.musinsa.category.domain.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.musinsa.category.api.dto.ApiResponseDto;
import org.musinsa.category.api.dto.BrandProductRequestDto;
import org.musinsa.category.domain.dto.BrandDto;
import org.musinsa.category.domain.dto.ProductDto;
import org.musinsa.category.domain.entity.Brand;
import org.musinsa.category.domain.entity.Product;
import org.musinsa.category.domain.repository.BrandRepository;

@ExtendWith(MockitoExtension.class)
class BrandProductServiceTest {

    @Mock
    private BrandRepository brandRepository;

    @InjectMocks
    private BrandProductService brandProductService;

    private BrandProductRequestDto requestDto;
    private Brand brand;

    @BeforeEach
    void setUp() {
        BrandDto brandDto = new BrandDto();
        brandDto.setId(1L);
        brandDto.setName("Test Brand");

        ProductDto productDto = new ProductDto();
        productDto.setId(1L);
        productDto.setCategory("Test Category");
        productDto.setPrice(1000L);

        requestDto = new BrandProductRequestDto();
        requestDto.setBrand(brandDto);
        requestDto.setProducts(List.of(productDto));

        brand = new Brand();
        brand.setId(1L);
        brand.setName("Test Brand");
    }

    @Test
    void testAddBrandAndProducts() {
        when(brandRepository.save(any(Brand.class))).thenReturn(brand);

        ApiResponseDto response = brandProductService.addBrandAndProducts(requestDto);

        assertTrue(response.isSuccess());
        assertEquals("Brand and products added successfully", response.getMessage());
        verify(brandRepository, times(1)).save(any(Brand.class));
    }

    @Test
    void testUpdateBrandAndProducts() {
        // Arrange
        Brand existingBrand = new Brand();
        existingBrand.setId(1L);
        existingBrand.setName("Existing Brand");
        existingBrand.setVersion(1L);

        Product existingProduct = new Product();
        existingProduct.setId(1L);
        existingProduct.setCategory("Existing Category");
        existingProduct.setPrice(500L);
        existingProduct.setVersion(1L);
        existingBrand.addProduct(existingProduct);

        when(brandRepository.findById(1L)).thenReturn(Optional.of(existingBrand));

        BrandDto updatedBrandDto = new BrandDto();
        updatedBrandDto.setId(1L);
        updatedBrandDto.setName("Updated Brand");

        ProductDto updatedProductDto = new ProductDto();
        updatedProductDto.setId(1L);
        updatedProductDto.setCategory("Updated Category");
        updatedProductDto.setPrice(1000L);

        BrandProductRequestDto requestDto = new BrandProductRequestDto();
        requestDto.setBrand(updatedBrandDto);
        requestDto.setProducts(Arrays.asList(updatedProductDto));

        ApiResponseDto response = brandProductService.updateBrandAndProducts(requestDto);

        assertTrue(response.isSuccess());
        assertEquals("Brand and products updated successfully", response.getMessage());

        verify(brandRepository, times(1)).findById(1L);

        assertEquals("Updated Brand", existingBrand.getName());
        assertEquals(2L, existingBrand.getVersion());
        assertNotNull(existingBrand.getLastModifiedDate());

        Product updatedProduct = existingBrand.getProducts().get(0);
        assertEquals("Updated Category", updatedProduct.getCategory());
        assertEquals(1000L, updatedProduct.getPrice());
        assertEquals(2L, updatedProduct.getVersion());
        assertNotNull(updatedProduct.getLastModifiedDate());
    }

    @Test
    void testUpdateBrandAndProducts_BrandNotFound() {
        when(brandRepository.findById(1L)).thenReturn(Optional.empty());

        ApiResponseDto response = brandProductService.updateBrandAndProducts(requestDto);

        assertFalse(response.isSuccess());
        assertTrue(response.getMessage().contains("Failed to update brand and products"));
        verify(brandRepository, times(1)).findById(1L);
    }

    @Test
    void testDeleteBrandAndProducts() {
        // Arrange
        Brand existingBrand = new Brand();
        existingBrand.setId(1L);
        existingBrand.setName("Existing Brand");
        existingBrand.setVersion(1L);

        Product existingProduct = new Product();
        existingProduct.setId(1L);
        existingProduct.setCategory("Existing Category");
        existingProduct.setPrice(500L);
        existingProduct.setVersion(1L);
        existingBrand.addProduct(existingProduct);

        // when
        when(brandRepository.findById(1L)).thenReturn(Optional.of(existingBrand));

        BrandDto brandDto = new BrandDto();
        brandDto.setId(1L);

        BrandProductRequestDto requestDto = new BrandProductRequestDto();
        requestDto.setBrand(brandDto);

        ApiResponseDto response = brandProductService.deleteBrandAndProducts(requestDto);

        // then
        assertTrue(response.isSuccess());
        assertEquals("Brand and products deleted successfully", response.getMessage());

        verify(brandRepository, times(1)).findById(1L);

        assertNotNull(existingBrand.getDeletedDate());
        assertNotNull(existingBrand.getLastModifiedDate());
        assertEquals(2L, existingBrand.getVersion());

        Product deletedProduct = existingBrand.getProducts().get(0);
        assertNotNull(deletedProduct.getDeletedDate());
        assertNotNull(deletedProduct.getLastModifiedDate());
        assertEquals(2L, deletedProduct.getVersion());
    }

    @Test
    void testDeleteBrandAndProducts_BrandNotFound() {
        when(brandRepository.findById(1L)).thenReturn(Optional.empty());

        ApiResponseDto response = brandProductService.deleteBrandAndProducts(requestDto);

        assertFalse(response.isSuccess());
        assertTrue(response.getMessage().contains("Failed to delete brand and products"));
        verify(brandRepository, times(1)).findById(1L);
    }

    @Test
    void testAddBrandAndProducts_Exception() {
        when(brandRepository.save(any(Brand.class))).thenThrow(new RuntimeException("Test exception"));

        ApiResponseDto response = brandProductService.addBrandAndProducts(requestDto);

        assertFalse(response.isSuccess());
        assertTrue(response.getMessage().contains("Failed to add brand and products"));
        verify(brandRepository, times(1)).save(any(Brand.class));
    }

    @Test
    void testAddBrandAndProducts_NullBrandName() {
        BrandDto brandDto = new BrandDto();
        brandDto.setId(1L);
        brandDto.setName(null);

        requestDto.setBrand(brandDto);

        ApiResponseDto response = brandProductService.addBrandAndProducts(requestDto);

        assertFalse(response.isSuccess());
        assertTrue(response.getMessage().contains("Failed to add brand and products"));
        verify(brandRepository, never()).save(any(Brand.class));
    }

    @Test
    void testAddBrandAndProducts_EmptyProductList() {
        BrandDto brandDto = new BrandDto();
        brandDto.setId(1L);
        brandDto.setName("Test Brand");

        requestDto.setBrand(brandDto);
        requestDto.setProducts(Collections.emptyList());

        ApiResponseDto response = brandProductService.addBrandAndProducts(requestDto);

        assertFalse(response.isSuccess());
        assertTrue(response.getMessage().contains("Failed to add brand and products"));
        verify(brandRepository, never()).save(any(Brand.class));
    }

    @Test
    void testUpdateBrandAndProducts_NullProductCategory() {
        when(brandRepository.findById(1L)).thenReturn(Optional.of(brand));

        ProductDto productDto = new ProductDto();
        productDto.setId(1L);
        productDto.setCategory(null);
        productDto.setPrice(1000L);

        requestDto.setProducts(List.of(productDto));

        ApiResponseDto response = brandProductService.updateBrandAndProducts(requestDto);

        assertFalse(response.isSuccess());
        assertTrue(response.getMessage().contains("Failed to update brand and products"));
    }

    @Test
    void testUpdateBrandAndProducts_NegativePrice() {
        when(brandRepository.findById(1L)).thenReturn(Optional.of(brand));

        ProductDto productDto = new ProductDto();
        productDto.setId(1L);
        productDto.setCategory("Test Category");
        productDto.setPrice(-1000L);

        requestDto.setProducts(List.of(productDto));

        ApiResponseDto response = brandProductService.updateBrandAndProducts(requestDto);

        assertFalse(response.isSuccess());
        assertTrue(response.getMessage().contains("Failed to update brand and products"));
    }

    @Test
    void testDeleteBrandAndProducts_ConcurrentModification() {
        Brand existingBrand = new Brand();
        existingBrand.setId(1L);
        existingBrand.setName("Existing Brand");
        existingBrand.setVersion(1L);

        Product existingProduct = new Product();
        existingProduct.setId(1L);
        existingProduct.setCategory("Existing Category");
        existingProduct.setPrice(500L);
        existingProduct.setVersion(1L);
        existingBrand.addProduct(existingProduct);

        when(brandRepository.findById(1L)).thenReturn(Optional.of(existingBrand));

        BrandDto brandDto = new BrandDto();
        brandDto.setId(1L);
        requestDto.setBrand(brandDto);

        ApiResponseDto response = brandProductService.deleteBrandAndProducts(requestDto);

        assertTrue(response.isSuccess());
        assertEquals("Brand and products deleted successfully", response.getMessage());
        verify(brandRepository, times(1)).findById(1L);

        assertNotNull(existingBrand.getDeletedDate());
        assertNotNull(existingBrand.getLastModifiedDate());
        assertEquals(2L, existingBrand.getVersion());

        Product deletedProduct = existingBrand.getProducts().get(0);
        assertNotNull(deletedProduct.getDeletedDate());
        assertNotNull(deletedProduct.getLastModifiedDate());
        assertEquals(2L, deletedProduct.getVersion());
    }

}