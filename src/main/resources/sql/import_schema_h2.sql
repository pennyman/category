-- 브랜드 테이블 생성
CREATE TABLE brands
(
    id        INT AUTO_INCREMENT PRIMARY KEY COMMENT '브랜드 ID',
    name      VARCHAR(255) NOT NULL UNIQUE COMMENT '브랜드 이름',
    CREATE_DT DATETIME     NOT NULL COMMENT '생성일시',
    UPDATE_DT DATETIME DEFAULT NULL COMMENT '수정일시',
    DEL_DT    DATETIME DEFAULT NULL COMMENT '삭제일시',
    VER_NO    BIGINT(20) UNSIGNED NOT NULL COMMENT '버전번호'
) COMMENT '브랜드 정보 테이블';

-- 상품 가격 테이블 생성
CREATE TABLE products
(
    id        INT AUTO_INCREMENT PRIMARY KEY COMMENT '상품 ID',
    brand_id  INT         NOT NULL COMMENT '브랜드 ID (brands 테이블의 FK)',
    category  VARCHAR(50) NOT NULL COMMENT '상품 카테고리',
    price     BIGINT(20)  NOT NULL COMMENT '상품 가격',
    CREATE_DT DATETIME    NOT NULL COMMENT '생성일시',
    UPDATE_DT DATETIME DEFAULT NULL COMMENT '수정일시',
    DEL_DT    DATETIME DEFAULT NULL COMMENT '삭제일시',
    VER_NO    BIGINT(20) UNSIGNED NOT NULL COMMENT '버전번호',
    FOREIGN KEY (brand_id) REFERENCES brands (id),
    UNIQUE (brand_id, category)
) COMMENT '상품 정보 테이블';