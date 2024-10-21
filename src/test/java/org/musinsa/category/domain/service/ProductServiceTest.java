package org.musinsa.category.domain.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.musinsa.category.api.dto.LowestPriceInfoDto;
import org.musinsa.category.domain.entity.Brand;
import org.musinsa.category.domain.entity.Product;
import org.musinsa.category.domain.repository.ProductRepository;
import org.musinsa.category.exception.CustomException;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    @BeforeEach
    void setUp() {
    }

    @Test
    void testGetLowestPriceByCategory() {
        // given
        List<Object[]> mockResult = Arrays.asList(
                new Object[]{"상의", "BrandA", 10000L},
                new Object[]{"바지", "BrandB", 20000L}
        );
        // when
        when(productRepository.findLowestPriceByCategory()).thenReturn(mockResult);

        LowestPriceInfoDto result = productService.getLowestPriceByCategory();

        // then
        assertNotNull(result);
        assertEquals(2, result.getCategories().size());
        assertEquals(30000L, result.getTotalPrice());
        assertEquals("상의", result.getCategories().get(0).getCategory());
        assertEquals("바지", result.getCategories().get(1).getCategory());
    }

    @Test
    void testGetLowestPriceBrandInfo() {
        // given
        List<Object[]> mockResult = new ArrayList<>();
        mockResult.add(new Object[]{"BrandA", 100000L});
        when(productRepository.findBrandWithLowestTotalPrice()).thenReturn(mockResult);

        Brand brand = new Brand();
        brand.setName("BrandA");

        Product product1 = new Product();
        product1.setCategory("상의");
        product1.setPrice(10000L);
        product1.setBrand(brand);

        Product product2 = new Product();
        product2.setCategory("바지");
        product2.setPrice(20000L);
        product2.setBrand(brand);

        // when
        when(productRepository.findByBrandNameAndDeletedDateIsNull("BrandA"))
                .thenReturn(Arrays.asList(product1, product2));

        Map<String, Object> result = productService.getLowestPriceBrandInfo();

        // then
        assertNotNull(result);
        assertTrue(result.containsKey("최저가"));
        Map<String, Object> lowestPrice = (Map<String, Object>) result.get("최저가");
        assertEquals("BrandA", lowestPrice.get("브랜드"));
        assertEquals("100,000", lowestPrice.get("총액"));
        List<Map<String, String>> categories = (List<Map<String, String>>) lowestPrice.get("카테고리");
        assertEquals(2, categories.size());
    }

    @Test
    void testGetCategoryPriceInfo() {
        // given
        Brand brandA = new Brand();
        brandA.setName("BrandA");

        Brand brandB = new Brand();
        brandB.setName("BrandB");

        Product lowestProduct = new Product();
        lowestProduct.setCategory("상의");
        lowestProduct.setPrice(10000L);
        lowestProduct.setBrand(brandA);

        Product highestProduct = new Product();
        highestProduct.setCategory("상의");
        highestProduct.setPrice(20000L);
        highestProduct.setBrand(brandB);

        // when
        when(productRepository.findLowestPriceProductsByCategory("상의"))
                .thenReturn(List.of(lowestProduct));
        when(productRepository.findHighestPriceProductsByCategory("상의"))
                .thenReturn(List.of(highestProduct));

        Map<String, Object> result = productService.getCategoryPriceInfo("상의");

        // then
        assertNotNull(result);
        assertEquals("상의", result.get("카테고리"));
        List<Map<String, String>> lowestPrice = (List<Map<String, String>>) result.get("최저가");
        List<Map<String, String>> highestPrice = (List<Map<String, String>>) result.get("최고가");
        assertEquals(1, lowestPrice.size());
        assertEquals(1, highestPrice.size());
        assertEquals("BrandA", lowestPrice.get(0).get("브랜드"));
        assertEquals("10,000", lowestPrice.get(0).get("가격"));
        assertEquals("BrandB", highestPrice.get(0).get("브랜드"));
        assertEquals("20,000", highestPrice.get(0).get("가격"));
    }

    @Test
    void testGetLowestPriceByCategory_ExceptionHandling() {
        when(productRepository.findLowestPriceByCategory()).thenThrow(new RuntimeException("Database error"));

        assertThrows(CustomException.class, () -> productService.getLowestPriceByCategory());
    }

    @Test
    void testGetLowestPriceByCategory_EmptyResult() {
        // given
        when(productRepository.findLowestPriceByCategory()).thenReturn(Collections.emptyList());

        // when
        LowestPriceInfoDto result = productService.getLowestPriceByCategory();

        // then
        assertNotNull(result, "결과 객체는 null이 아니어야 합니다.");
        assertTrue(result.getCategories().isEmpty(), "카테고리 리스트가 비어있어야 합니다.");
        assertEquals(0L, result.getTotalPrice(), "총 가격은 0이어야 합니다.");
    }

    @Test
    void testGetLowestPriceBrandInfo_EmptyResult() {
        // given
        when(productRepository.findBrandWithLowestTotalPrice()).thenReturn(Collections.emptyList());

        // when & then
        CustomException exception = assertThrows(CustomException.class,
                () -> productService.getLowestPriceBrandInfo());

        assertEquals("PRODUCT_RETRIEVAL_FAILED", exception.getErrorCode());
        assertTrue(exception.getMessage().contains("Failed to add product in getLowestPriceBrandInfo method"));
    }

    @Test
    void testGetCategoryPriceInfo_NonExistentCategory() {
        // given
        String nonExistentCategory = "NonExistentCategory";
        when(productRepository.findLowestPriceProductsByCategory(nonExistentCategory))
                .thenReturn(Collections.emptyList());
        when(productRepository.findHighestPriceProductsByCategory(nonExistentCategory))
                .thenReturn(Collections.emptyList());

        // when
        Map<String, Object> result = productService.getCategoryPriceInfo(nonExistentCategory);

        // then
        assertNotNull(result);
        assertEquals(nonExistentCategory, result.get("카테고리"));
        assertTrue(((List<?>) result.get("최저가")).isEmpty());
        assertTrue(((List<?>) result.get("최고가")).isEmpty());
    }

    @Test
    void testGetCategoryPriceInfo_EmptyCategory() {
        // given
        String emptyCategory = "";

        // when
        Map<String, Object> result = productService.getCategoryPriceInfo(emptyCategory);

        // then
        assertNotNull(result, "결과 객체는 null이 아니어야 합니다.");
        assertEquals(emptyCategory, result.get("카테고리"), "카테고리는 빈 문자열이어야 합니다.");
        assertTrue(((List<?>) result.get("최저가")).isEmpty(), "최저가 리스트가 비어있어야 합니다.");
        assertTrue(((List<?>) result.get("최고가")).isEmpty(), "최고가 리스트가 비어있어야 합니다.");
    }

    @Test
    void testGetLowestPriceByCategory_RepositoryException() {
        // given
        when(productRepository.findLowestPriceByCategory()).thenThrow(new RuntimeException("Database error"));

        // when & then
        assertThrows(CustomException.class, () -> productService.getLowestPriceByCategory());
    }

    @Test
    void testGetLowestPriceBrandInfo_RepositoryException() {
        // given
        when(productRepository.findBrandWithLowestTotalPrice()).thenThrow(new RuntimeException("Database error"));

        // when & then
        assertThrows(CustomException.class, () -> productService.getLowestPriceBrandInfo());
    }

    @Test
    void testGetCategoryPriceInfo_RepositoryException() {
        // given
        when(productRepository.findLowestPriceProductsByCategory(anyString())).thenThrow(new RuntimeException("Database error"));

        // when & then
        assertThrows(CustomException.class, () -> productService.getCategoryPriceInfo("상의"));
    }

}