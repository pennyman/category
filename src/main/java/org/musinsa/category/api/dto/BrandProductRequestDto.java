package org.musinsa.category.api.dto;

import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.musinsa.category.domain.dto.BrandDto;
import org.musinsa.category.domain.dto.ProductDto;

@NoArgsConstructor
@Getter
@Setter
public class BrandProductRequestDto {

    private BrandDto brand;
    private List<ProductDto> products;

    public BrandProductRequestDto(BrandDto brand, List<ProductDto> products) {
        this.brand = brand;
        this.products = products;
    }
}
