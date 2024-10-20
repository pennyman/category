package org.musinsa.category.domain.service;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.musinsa.category.api.dto.LowestPriceInfoDto;
import org.musinsa.category.domain.dto.CategoryPriceDto;
import org.musinsa.category.domain.entity.Brand;
import org.musinsa.category.domain.entity.Product;
import org.musinsa.category.domain.repository.BrandRepository;
import org.musinsa.category.domain.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@SpringBootTest
@Transactional
class ProductServiceIntegrationTest {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private BrandRepository brandRepository;

    @BeforeEach
    void setUp() {
        // 테스트 데이터 설정
        Brand brand1 = new Brand();
        brand1.setName("브랜드A");
        brand1.setCreatedDate(LocalDateTime.now());
        brand1.setVersion(1L);
        brandRepository.save(brand1);

        Brand brand2 = new Brand();
        brand2.setName("브랜드B");
        brand2.setCreatedDate(LocalDateTime.now());
        brand2.setVersion(1L);
        brandRepository.save(brand2);

        createProduct(brand1, "상의", 10000L);
        createProduct(brand1, "바지", 20000L);
        createProduct(brand2, "상의", 15000L);
        createProduct(brand2, "스니커즈", 25000L);
    }

    private void createProduct(Brand brand, String category, Long price) {
        Product product = new Product();
        product.setBrand(brand);
        product.setCategory(category);
        product.setPrice(price);
        product.setCreatedDate(LocalDateTime.now());
        product.setVersion(1L);
        productRepository.save(product);
    }

    @Test
    void 각카테고리별_최저가_총가격_확인성공() {
        LowestPriceInfoDto result = productService.getLowestPriceByCategory();

        assertNotNull(result);
        assertEquals(3, result.getCategories().size());
        assertEquals(55000L, result.getTotalPrice());

        CategoryPriceDto firstCategory = result.getCategories().get(0);
        assertEquals("상의", firstCategory.getCategory());
        assertEquals("브랜드A", firstCategory.getBrand());
        assertEquals(10000L, firstCategory.getPrice());
    }

    @Test
    void 전체가격_가장낮은_브랜드_확인성공() {
        Map<String, Object> result = productService.getLowestPriceBrandInfo();

        assertNotNull(result);
        assertTrue(result.containsKey("최저가"));

        Map<String, Object> lowestPrice = (Map<String, Object>) result.get("최저가");
        assertEquals("브랜드A", lowestPrice.get("브랜드"));
        assertEquals("30,000", lowestPrice.get("총액"));

        List<Map<String, String>> categories = (List<Map<String, String>>) lowestPrice.get("카테고리");
        assertEquals(2, categories.size());
        assertEquals("상의", categories.get(0).get("카테고리"));
        assertEquals("10,000", categories.get(0).get("가격"));
    }

    @Test
    void 특정카테고리_최저가_최고가_확인성공() {
        Map<String, Object> result = productService.getCategoryPriceInfo("상의");

        assertNotNull(result);
        assertEquals("상의", result.get("카테고리"));

        List<Map<String, String>> lowestPrice = (List<Map<String, String>>) result.get("최저가");
        assertEquals(1, lowestPrice.size());
        assertEquals("브랜드A", lowestPrice.get(0).get("브랜드"));
        assertEquals("10,000", lowestPrice.get(0).get("가격"));

        List<Map<String, String>> highestPrice = (List<Map<String, String>>) result.get("최고가");
        assertEquals(1, highestPrice.size());
        assertEquals("브랜드B", highestPrice.get(0).get("브랜드"));
        assertEquals("15,000", highestPrice.get(0).get("가격"));
    }


}