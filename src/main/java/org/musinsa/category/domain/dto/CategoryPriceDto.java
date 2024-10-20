package org.musinsa.category.domain.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CategoryPriceDto {
    private String category;
    private String brand;
    private Long price;

    public CategoryPriceDto(String category, String brand, Long price) {
        this.category = category;
        this.brand = brand;
        this.price = price;
    }
}

