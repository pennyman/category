package org.musinsa.category.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.CREATED;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.musinsa.category.api.dto.ApiResponseDto;
import org.musinsa.category.api.dto.BrandProductRequestDto;
import org.musinsa.category.api.dto.LowestPriceInfoDto;
import org.musinsa.category.domain.dto.CategoryPriceDto;
import org.musinsa.category.domain.service.BrandProductService;
import org.musinsa.category.domain.service.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class ProductControllerTest {

    @Mock
    private ProductService productService;

    @Mock
    private BrandProductService brandProductService;

    @InjectMocks
    private ProductController productController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetLowestPriceByCategory() {
        LowestPriceInfoDto expectedDto = new LowestPriceInfoDto(List.of(new CategoryPriceDto("카테고리", "브랜드", 1000L)), 1000L);
        when(productService.getLowestPriceByCategory()).thenReturn(expectedDto);

        LowestPriceInfoDto result = productController.getLowestPriceByCategory();

        assertEquals(expectedDto, result);
        verify(productService).getLowestPriceByCategory();
    }

    @Test
    void testGetLowestPriceBrandInfo() {
        Map<String, Object> expectedMap = new HashMap<>();
        expectedMap.put("브랜드", "테스트브랜드");
        when(productService.getLowestPriceBrandInfo()).thenReturn(expectedMap);

        Map<String, Object> result = productController.getLowestPriceBrandInfo();

        assertEquals(expectedMap, result);
        verify(productService).getLowestPriceBrandInfo();
    }

    @Test
    void testGetCategoryPriceInfo() {
        String category = "테스트카테고리";
        Map<String, Object> expectedMap = new HashMap<>();
        expectedMap.put("카테고리", category);
        when(productService.getCategoryPriceInfo(category)).thenReturn(expectedMap);

        Map<String, Object> result = productController.getCategoryPriceInfo(category);

        assertEquals(expectedMap, result);
        verify(productService).getCategoryPriceInfo(category);
    }

    @Test
    void testAddBrandAndProducts() {
        BrandProductRequestDto requestDto = new BrandProductRequestDto();
        ApiResponseDto responseDto = new ApiResponseDto(true, "성공");
        when(brandProductService.addBrandAndProducts(requestDto)).thenReturn(responseDto);

        ResponseEntity<ApiResponseDto> response = productController.addBrandAndProducts(requestDto);

        assertEquals(CREATED, response.getStatusCode());
        assertEquals(responseDto, response.getBody());
        verify(brandProductService).addBrandAndProducts(requestDto);
    }

    @Test
    void testUpdateBrandAndProducts() {
        BrandProductRequestDto requestDto = new BrandProductRequestDto();
        ApiResponseDto responseDto = new ApiResponseDto(true, "성공");
        when(brandProductService.updateBrandAndProducts(requestDto)).thenReturn(responseDto);

        ResponseEntity<ApiResponseDto> response = productController.updateBrandAndProducts(requestDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(responseDto, response.getBody());
        verify(brandProductService).updateBrandAndProducts(requestDto);
    }

    @Test
    void testDeleteBrandAndProducts() {
        BrandProductRequestDto requestDto = new BrandProductRequestDto();
        ApiResponseDto responseDto = new ApiResponseDto(true, "성공");
        when(brandProductService.deleteBrandAndProducts(requestDto)).thenReturn(responseDto);

        ResponseEntity<ApiResponseDto> response = productController.deleteBrandAndProducts(requestDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(responseDto, response.getBody());
        verify(brandProductService).deleteBrandAndProducts(requestDto);
    }
}