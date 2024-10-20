package org.musinsa.category.api;

import static org.springframework.http.HttpStatus.CREATED;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.musinsa.category.api.dto.ApiResponseDto;
import org.musinsa.category.api.dto.BrandProductRequestDto;
import org.musinsa.category.api.dto.LowestPriceInfoDto;
import org.musinsa.category.domain.service.BrandProductService;
import org.musinsa.category.domain.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/product/v1")
@OpenAPIDefinition(
        info = @Info(
                title = "Product API",
                version = "1.0",
                description = "API for managing products"
        )
)
@Tag(name = "products", description = "상품 관리")
public class ProductController {

    private final ProductService productService;
    private final BrandProductService brandProductService;

    @Operation(
            summary = "카테고리별 최저가 상품 조회",
            description = "모든 카테고리에 대해 최저가 상품의 브랜드와 가격, 총액을 조회합니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "성공적으로 조회됨",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = LowestPriceInfoDto.class)
                            )
                    ),
                    @ApiResponse(responseCode = "500", description = "서버 오류")
            }
    )
    @GetMapping("/lowest-price")
    public LowestPriceInfoDto getLowestPriceByCategory() {
        return productService.getLowestPriceByCategory();
    }

    @Operation(
            summary = "단일브랜드 브랜드와 카테고리의 상품가격,총액 조회",
            description = "단일 브랜드로 모든 카테고리에 대해 최저가 상품의 브랜드와 카테고리 가격, 총액을 조회합니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "성공적으로 조회됨",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = Map.class)
                            )
                    ),
                    @ApiResponse(responseCode = "500", description = "서버 오류")
            }
    )
    @GetMapping("/brand/lowest-price")
    public Map<String, Object> getLowestPriceBrandInfo() {
        return productService.getLowestPriceBrandInfo();
    }

    @Operation(
            summary = "카테고리 이름 최저,최고 가격 브랜드와 상품가격 조회",
            description = "카테고리 이름 최저,최고 가격 브랜드와 상품가격 조회합니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "성공적으로 조회됨",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = Map.class)
                            )
                    ),
                    @ApiResponse(responseCode = "500", description = "서버 오류")
            }
    )
    @GetMapping("/category/price")
    public Map<String, Object> getCategoryPriceInfo(@RequestParam String category) {
        return productService.getCategoryPriceInfo(category);
    }

    @Operation(
            summary = "브랜드 및 상품 추가",
            description = "브랜드 및 상품 추가합니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "성공적으로 조회됨",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ResponseEntity.class)
                            )
                    ),
                    @ApiResponse(responseCode = "500", description = "서버 오류")
            }
    )
    @PostMapping
    public ResponseEntity<ApiResponseDto> addBrandAndProducts(@RequestBody BrandProductRequestDto request) {
        ApiResponseDto response = brandProductService.addBrandAndProducts(request);
        return ResponseEntity.status(CREATED).body(response);
    }

    @Operation(
            summary = "브랜드 및 상품 업데이트",
            description = "브랜드 및 상품 업데이트 합니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "성공적으로 조회됨",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ResponseEntity.class)
                            )
                    ),
                    @ApiResponse(responseCode = "500", description = "서버 오류")
            }
    )
    @PutMapping
    public ResponseEntity<ApiResponseDto> updateBrandAndProducts(@RequestBody BrandProductRequestDto request) {
        ApiResponseDto response = brandProductService.updateBrandAndProducts(request);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "브랜드 및 상품 삭제",
            description = "브랜드 및 상품 삭제 합니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "성공적으로 조회됨",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ResponseEntity.class)
                            )
                    ),
                    @ApiResponse(responseCode = "500", description = "서버 오류")
            }
    )
    @DeleteMapping
    public ResponseEntity<ApiResponseDto> deleteBrandAndProducts(@RequestBody BrandProductRequestDto request) {
        ApiResponseDto response = brandProductService.deleteBrandAndProducts(request);
        return ResponseEntity.ok(response);
    }
}
