package org.musinsa.category.domain.service;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.musinsa.category.api.dto.LowestPriceInfoDto;
import org.musinsa.category.domain.dto.CategoryPriceDto;
import org.musinsa.category.domain.entity.Product;
import org.musinsa.category.domain.repository.ProductRepository;
import org.musinsa.category.exception.CustomException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    private static final List<String> CATEGORY_ORDER = Arrays.asList(
            "상의", "아우터", "바지", "스니커즈", "가방", "모자", "양말", "액세서리"
    );

    public LowestPriceInfoDto getLowestPriceByCategory() {
        try {
            List<CategoryPriceDto> categoryPriceList = productRepository.findLowestPriceByCategory().stream()
                    .map(result -> new CategoryPriceDto(
                            (String) result[0],  // category
                            (String) result[1],  // brand
                            ((Number) result[2]).longValue()  // price
                    ))
                    .toList();

            List<CategoryPriceDto> sortedCategoryPriceList = categoryPriceList.stream()
                    .sorted(Comparator.<CategoryPriceDto, Integer>comparing(dto -> {
                        int index = CATEGORY_ORDER.indexOf(dto.getCategory());
                        return index == -1 ? Integer.MAX_VALUE : index;
                    }).thenComparing(CategoryPriceDto::getCategory))
                    .toList();

            return new LowestPriceInfoDto(
                    sortedCategoryPriceList,
                    sortedCategoryPriceList.stream().mapToLong(CategoryPriceDto::getPrice).sum()
            );
        } catch (Exception e) {
            throw new CustomException("PRODUCT_ADD_FAILED", "Failed to add product: " + e.getMessage());
        }
    }

    public Map<String, Object> getLowestPriceBrandInfo() {
        try {
            List<Object[]> result = productRepository.findBrandWithLowestTotalPrice();

            String lowestPriceBrand = (String) result.get(0)[0];
            Long totalPrice = (Long) result.get(0)[1];

            List<Product> products = productRepository.findByBrandNameAndDeletedDateIsNull(lowestPriceBrand);

            return createLowestPriceResponse(lowestPriceBrand, products, totalPrice);
        } catch (Exception e) {
            throw new CustomException("PRODUCT_ADD_FAILED", "Failed to add product: " + e.getMessage());
        }
    }

    private Map<String, Object> createLowestPriceResponse(String brand, List<Product> products, Long totalPrice) {
        Map<String, Object> lowestPrice = new LinkedHashMap<>();

        lowestPrice.put("브랜드", brand);
        lowestPrice.put("카테고리", formatProductCategories(products));
        lowestPrice.put("총액", formatPrice(totalPrice));

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("최저가", lowestPrice);

        return response;
    }

    private List<Map<String, String>> formatProductCategories(List<Product> products) {
        return products.stream()
                .sorted(Comparator.comparingInt(p -> CATEGORY_ORDER.indexOf(p.getCategory())))
                .map(p -> Map.of(
                        "카테고리", p.getCategory(),
                        "가격", formatPrice(p.getPrice())
                ))
                .collect(Collectors.toList());
    }

    private String formatPrice(Long price) {
        return String.format("%,d", price);
    }

    public Map<String, Object> getCategoryPriceInfo(String category) {
        try {
            List<Product> lowestPriceProducts = productRepository.findLowestPriceProductsByCategory(category);
            List<Product> highestPriceProducts = productRepository.findHighestPriceProductsByCategory(category);

            Map<String, Object> result = new LinkedHashMap<>();
            result.put("카테고리", category);
            result.put("최저가", mapToResponse(lowestPriceProducts));
            result.put("최고가", mapToResponse(highestPriceProducts));

            return result;
        } catch (Exception e) {
            throw new CustomException("PRODUCT_ADD_FAILED", "Failed to add product: " + e.getMessage());
        }
    }

    private List<Map<String, String>> mapToResponse(List<Product> products) {
        return products.stream().map(p -> {
            Map<String, String> map = new HashMap<>();
            map.put("브랜드", p.getBrand().getName());
            map.put("가격", formatPrice(p.getPrice()));
            return map;
        }).collect(Collectors.toList());
    }

}
