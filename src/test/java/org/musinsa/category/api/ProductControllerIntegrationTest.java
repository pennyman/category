package org.musinsa.category.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.musinsa.category.api.dto.ApiResponseDto;
import org.musinsa.category.api.dto.BrandProductRequestDto;
import org.musinsa.category.api.dto.LowestPriceInfoDto;
import org.musinsa.category.domain.dto.BrandDto;
import org.musinsa.category.domain.dto.CategoryPriceDto;
import org.musinsa.category.domain.dto.ProductDto;
import org.musinsa.category.domain.service.BrandProductService;
import org.musinsa.category.domain.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ProductControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProductService productService;

    @MockBean
    private BrandProductService brandProductService;

    @Test
    @DisplayName("카테고리별 최저가 상품 조회 테스트")
    void testGetLowestPriceByCategory() throws Exception {
        // Given
        LowestPriceInfoDto lowestPriceInfo = new LowestPriceInfoDto(
                List.of(new CategoryPriceDto("상의", "A", 10000L)),
                10000L
        );
        when(productService.getLowestPriceByCategory()).thenReturn(lowestPriceInfo);

        // When & Then
        mockMvc.perform(get("/product/v1/lowest-price")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.categories[0].category").value("상의"))
                .andExpect(jsonPath("$.categories[0].brand").value("A"))
                .andExpect(jsonPath("$.categories[0].price").value(10000L))
                .andExpect(jsonPath("$.totalPrice").value(10000L));
    }

    @Test
    @DisplayName("단일 브랜드 최저가 상품 조회 테스트")
    void testGetLowestPriceBrandInfo() throws Exception {
        // Given
        Map<String, Object> brandInfo = new HashMap<>();
        brandInfo.put("brand", "A");
        brandInfo.put("totalPrice", 20000L);
        when(productService.getLowestPriceBrandInfo()).thenReturn(brandInfo);

        // When & Then
        mockMvc.perform(get("/product/v1/brand/lowest-price")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.brand").value("A"))
                .andExpect(jsonPath("$.totalPrice").value(20000L));
    }

    @Test
    @DisplayName("카테고리별 최저 및 최고 가격 상품 조회 테스트")
    void testGetCategoryPriceInfo() throws Exception {
        // Given
        String category = "상의";
        Map<String, Object> categoryInfo = new HashMap<>();
        categoryInfo.put("lowestPrice", 10000L);
        categoryInfo.put("highestPrice", 50000L);
        when(productService.getCategoryPriceInfo(category)).thenReturn(categoryInfo);

        // When & Then
        mockMvc.perform(get("/product/v1/category/price")
                        .param("category", category)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lowestPrice").value(10000L))
                .andExpect(jsonPath("$.highestPrice").value(50000L));
    }

    @Test
    @DisplayName("브랜드 및 상품 추가 테스트")
    void testAddBrandAndProducts() throws Exception {
        // Given
        BrandDto brandDto = new BrandDto(1L, "A");
        List<ProductDto> productDtos = List.of(
                new ProductDto(1L, "상의", 11200L, "A")
        );
        BrandProductRequestDto requestDto = new BrandProductRequestDto(brandDto, productDtos);
        ApiResponseDto responseDto = new ApiResponseDto(true, "Brand and products added successfully");
        when(brandProductService.addBrandAndProducts(any())).thenReturn(responseDto);

        // When & Then
        mockMvc.perform(post("/product/v1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Brand and products added successfully"));
    }

    @Test
    @DisplayName("브랜드 및 상품 업데이트 테스트")
    void testUpdateBrandAndProducts() throws Exception {
        // Given
        BrandDto brandDto = new BrandDto(1L, "A");
        List<ProductDto> productDtos = List.of(
                new ProductDto(1L, "상의", 12200L, "A")
        );
        BrandProductRequestDto requestDto = new BrandProductRequestDto(brandDto, productDtos);
        ApiResponseDto responseDto = new ApiResponseDto(true, "Brand and products updated successfully");
        when(brandProductService.updateBrandAndProducts(any())).thenReturn(responseDto);

        // When & Then
        mockMvc.perform(put("/product/v1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Brand and products updated successfully"));
    }

    @Test
    @DisplayName("브랜드 및 상품 삭제 테스트")
    void testDeleteBrandAndProducts() throws Exception {
        // Given
        BrandDto brandDto = new BrandDto(1L, "A");
        List<ProductDto> productDtos = List.of(
                new ProductDto(1L, "상의", 11200L, "A")
        );
        BrandProductRequestDto requestDto = new BrandProductRequestDto(brandDto, productDtos);
        ApiResponseDto responseDto = new ApiResponseDto(true, "Brand and products deleted successfully");
        when(brandProductService.deleteBrandAndProducts(any())).thenReturn(responseDto);

        // When & Then
        mockMvc.perform(delete("/product/v1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Brand and products deleted successfully"));
    }
}