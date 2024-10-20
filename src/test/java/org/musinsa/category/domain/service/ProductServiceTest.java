package org.musinsa.category.domain.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
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
        // Arrange
        List<Object[]> mockResult = Arrays.asList(
                new Object[]{"상의", "BrandA", 10000L},
                new Object[]{"바지", "BrandB", 20000L}
        );
        when(productRepository.findLowestPriceByCategory()).thenReturn(mockResult);

        // Act
        LowestPriceInfoDto result = productService.getLowestPriceByCategory();

        // Assert
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

        when(productRepository.findByBrandNameAndDeletedDateIsNull("BrandA"))
                .thenReturn(Arrays.asList(product1, product2));

        // when
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

        when(productRepository.findLowestPriceProductsByCategory("상의"))
                .thenReturn(Arrays.asList(lowestProduct));
        when(productRepository.findHighestPriceProductsByCategory("상의"))
                .thenReturn(Arrays.asList(highestProduct));

        // when
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
        // given
        when(productRepository.findLowestPriceByCategory()).thenThrow(new RuntimeException("Database error"));

        // then
        assertThrows(CustomException.class, () -> productService.getLowestPriceByCategory());
    }

}