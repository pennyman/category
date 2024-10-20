package org.musinsa.category.domain.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class BrandDto {
    private Long id;
    private String name;

    public BrandDto(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}
