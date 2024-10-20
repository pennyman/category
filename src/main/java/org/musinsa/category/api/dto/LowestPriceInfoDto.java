package org.musinsa.category.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.musinsa.category.domain.dto.CategoryPriceDto;

@NoArgsConstructor
@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LowestPriceInfoDto {

    private List<CategoryPriceDto> categories;
    private Long totalPrice;

    public LowestPriceInfoDto(List<CategoryPriceDto> categories, long totalPrice) {
        this.categories = categories;
        this.totalPrice = totalPrice;
    }
}
