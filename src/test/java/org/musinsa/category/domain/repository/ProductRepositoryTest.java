package org.musinsa.category.domain.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.musinsa.category.domain.entity.Brand;
import org.musinsa.category.domain.entity.Product;

@ExtendWith(MockitoExtension.class)
class ProductRepositoryTest {

    @Mock
    private ProductRepository productRepository;

    private final Brand mockBrand = new Brand();

    @BeforeEach
    void setUp() {
        mockBrand.setName("Brand1");
    }
    @Test
    @DisplayName("카테고리별 최저가 상품 조회 테스트")
    void testFindLowestPriceByCategory() {
        // Given
        List<Object[]> mockResults = List.of(
                new Object[]{"상의", "Brand1", 50000L},
                new Object[]{"아우터", "Brand2", 80000L}
        );
        Mockito.when(productRepository.findLowestPriceByCategory()).thenReturn(mockResults);

        // When
        List<Object[]> result = productRepository.findLowestPriceByCategory();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());

        Object[] consultation = result.get(0);
        assertEquals("상의", consultation[0]);
        assertEquals("Brand1", consultation[1]);
        assertEquals(50000L, consultation[2]);

        Object[] outerwear = result.get(1);
        assertEquals("아우터", outerwear[0]);
        assertEquals("Brand2", outerwear[1]);
        assertEquals(80000L, outerwear[2]);

        Mockito.verify(productRepository, Mockito.times(1)).findLowestPriceByCategory();
    }

    @Test
    @DisplayName("최저 총액을 가진 브랜드 조회 테스트")
    void testFindBrandWithLowestTotalPrice() {
        // Given
        List<Object[]> mockResults = List.of(
                new Object[]{"Brand1", 120000L},
                new Object[]{"Brand2", 180000L}
        );
        Mockito.when(productRepository.findBrandWithLowestTotalPrice()).thenReturn(mockResults);

        // When
        List<Object[]> result = productRepository.findBrandWithLowestTotalPrice();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());

        Object[] brand1 = result.get(0);
        assertEquals("Brand1", brand1[0]);
        assertEquals(120000L, ((Number) brand1[1]).longValue());

        Object[] brand2 = result.get(1);
        assertEquals("Brand2", brand2[0]);
        assertEquals(180000L, ((Number) brand2[1]).longValue());

        Mockito.verify(productRepository, Mockito.times(1)).findBrandWithLowestTotalPrice();
    }

    @Test
    @DisplayName("특정 브랜드의 상품 조회 테스트")
    void testFindByBrandNameAndDeletedDateIsNull() {
        // Given
        List<Product> mockProducts = List.of(
                createProduct(mockBrand, "A", 50000L),
                createProduct(mockBrand, "A", 70000L)
        );
        Mockito.when(productRepository.findByBrandNameAndDeletedDateIsNull("Brand1")).thenReturn(mockProducts);

        // When
        List<Product> result = productRepository.findByBrandNameAndDeletedDateIsNull("Brand1");

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());

        Product product1 = result.get(0);
        assertEquals("A", product1.getCategory());
        assertEquals(50000L, product1.getPrice());

        Product product2 = result.get(1);
        assertEquals("A", product2.getCategory());
        assertEquals(70000L, product2.getPrice());

        Mockito.verify(productRepository, Mockito.times(1)).findByBrandNameAndDeletedDateIsNull("Brand1");
    }

    @Test
    @DisplayName("카테고리별 최저가 상품 조회 테스트")
    void testFindLowestPriceProductsByCategory() {
        // Given
        List<Product> mockProducts = List.of(
                createProduct(mockBrand, "상의", 50000L)
        );
        Mockito.when(productRepository.findLowestPriceProductsByCategory("상의")).thenReturn(mockProducts);

        // When
        List<Product> result = productRepository.findLowestPriceProductsByCategory("상의");

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());

        Product product = result.get(0);
        assertEquals("상의", product.getCategory());
        assertEquals(50000L, product.getPrice());

        Mockito.verify(productRepository, Mockito.times(1)).findLowestPriceProductsByCategory("상의");
    }

    @Test
    @DisplayName("카테고리별 최고가 상품 조회 테스트")
    void testFindHighestPriceProductsByCategory() {
        // Given
        List<Product> mockProducts = List.of(
                createProduct(mockBrand, "아우터", 100000L)
        );
        Mockito.when(productRepository.findHighestPriceProductsByCategory("아우터")).thenReturn(mockProducts);

        // When
        List<Product> result = productRepository.findHighestPriceProductsByCategory("아우터");

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());

        Product product = result.get(0);
        assertEquals("아우터", product.getCategory());
        assertEquals(100000L, product.getPrice());

        Mockito.verify(productRepository, Mockito.times(1)).findHighestPriceProductsByCategory("아우터");
    }

    private Product createProduct(Brand brand, String category, Long price) {
        Product newProduct = new Product();
        newProduct.setBrand(brand);
        newProduct.setCategory(category);
        newProduct.setPrice(price);
        newProduct.setLastModifiedDate(LocalDateTime.now());
        newProduct.setVersion(1L);
        return newProduct;
    }
}
