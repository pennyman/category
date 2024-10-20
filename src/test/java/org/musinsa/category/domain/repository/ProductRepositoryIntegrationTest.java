

package org.musinsa.category.domain.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.musinsa.category.domain.entity.Brand;
import org.musinsa.category.domain.entity.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
class ProductRepositoryIntegrationTest {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private BrandRepository brandRepository;

    @BeforeEach
    void setUp() {
        setupTestData();
    }

    private void setupTestData() {
        Brand brand1 = createBrand("Brand A");
        Brand brand2 = createBrand("Brand B");

        createProduct("Category1", 1000L, brand1);
        createProduct("Category1", 1500L, brand2);
        createProduct("Category2", 2000L, brand1);
    }

    private Brand createBrand(String name) {
        Brand brand = new Brand();
        brand.setName(name);
        brand.setCreatedDate(LocalDateTime.now());
        brand.setVersion(1L);
        return brandRepository.save(brand);
    }

    private Product createProduct(String category, Long price, Brand brand) {
        Product product = new Product();
        product.setCategory(category);
        product.setPrice(price);
        product.setBrand(brand);
        product.setCreatedDate(LocalDateTime.now());
        product.setVersion(1L);
        return productRepository.save(product);
    }

    @Test
    @DisplayName("카테고리별 최저가 상품 조회 테스트")
    void testFindLowestPriceByCategory() {
        List<Object[]> results = productRepository.findLowestPriceByCategory();

        assertNotNull(results, "결과가 null이 아니어야 합니다.");
        assertEquals(2, results.size(), "2개의 카테고리 결과가 있어야 합니다.");

        assertCategoryResult(results.get(0), "Category1", "Brand A", 1000L);
        assertCategoryResult(results.get(1), "Category2", "Brand A", 2000L);
    }

    private void assertCategoryResult(Object[] result, String expectedCategory, String expectedBrand, Long expectedPrice) {
        assertEquals(expectedCategory, result[0], "카테고리가 일치해야 합니다.");
        assertEquals(expectedBrand, result[1], "브랜드가 일치해야 합니다.");
        assertEquals(expectedPrice, ((Number) result[2]).longValue(), "가격이 일치해야 합니다.");
    }

    @Test
    @DisplayName("최저 총액 브랜드 조회 테스트")
    void testFindBrandWithLowestTotalPrice() {
        List<Object[]> results = productRepository.findBrandWithLowestTotalPrice();

        assertNotNull(results, "결과가 null이 아니어야 합니다.");
        assertTrue(results.size() >= 1, "최소 1개 이상의 결과가 있어야 합니다.");

        Object[] lowestTotalPrice = results.get(0);
        assertEquals("Brand B", lowestTotalPrice[0], "최저 총액 브랜드는 'Brand B'여야 합니다.");
        assertEquals(1500L, ((Number) lowestTotalPrice[1]).longValue(), "Brand B의 총액은 1500이어야 합니다.");
    }

    @Test
    @DisplayName("브랜드별 삭제되지 않은 상품 조회 테스트")
    void testFindByBrandNameAndDeletedDateIsNull() {
        List<Product> products = productRepository.findByBrandNameAndDeletedDateIsNull("Brand A");

        assertNotNull(products, "결과가 null이 아니어야 합니다.");
        assertEquals(2, products.size(), "Brand A의 상품은 2개여야 합니다.");
        assertTrue(products.stream().allMatch(p -> p.getBrand().getName().equals("Brand A")), "모든 상품의 브랜드는 'Brand A'여야 합니다.");
    }

    @Test
    @DisplayName("카테고리별 최저가 상품 조회 테스트")
    void testFindLowestPriceProductsByCategory() {
        List<Product> products = productRepository.findLowestPriceProductsByCategory("Category1");

        assertNotNull(products, "결과가 null이 아니어야 합니다.");
        assertEquals(1, products.size(), "최저가 상품은 1개여야 합니다.");
        assertEquals(1000L, products.get(0).getPrice(), "최저가는 1000이어야 합니다.");
        assertEquals("Brand A", products.get(0).getBrand().getName(), "최저가 상품의 브랜드는 'Brand A'여야 합니다.");
    }

    @Test
    @DisplayName("카테고리별 최고가 상품 조회 테스트")
    void testFindHighestPriceProductsByCategory() {
        List<Product> products = productRepository.findHighestPriceProductsByCategory("Category1");

        assertNotNull(products, "결과가 null이 아니어야 합니다.");
        assertEquals(1, products.size(), "최고가 상품은 1개여야 합니다.");
        assertEquals(1500L, products.get(0).getPrice(), "최고가는 1500이어야 합니다.");
        assertEquals("Brand B", products.get(0).getBrand().getName(), "최고가 상품의 브랜드는 'Brand B'여야 합니다.");
    }
}

