package org.musinsa.category.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.musinsa.category.domain.AbstractDomain;

@Entity
@Getter
@Setter
@Table(name = "PRODUCT")
@EqualsAndHashCode(callSuper = false, exclude = "brand")
@ToString(exclude = "brand")
public class Product extends AbstractDomain<Product, Long> {
    private static final long serialVersionUID = 8803184959729142564L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "brand_id", nullable = false)
    private Brand brand;

    @Column(nullable = false, length = 20)
    private String category;

    @Column(nullable = false)
    private Long price;

}
