package org.musinsa.category.domain.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.musinsa.category.domain.AbstractDomain;

@Entity
@Getter
@Setter
@Table(name = "BRAND")
@EqualsAndHashCode(callSuper = false, exclude = "products")
@ToString(exclude = "products")
public class Brand extends AbstractDomain<Brand, Long> {
    private static final long serialVersionUID = 7901688954583211313L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private Long id;

    @Column(nullable = false, length = 50)
    private String name;

    @OneToMany(mappedBy = "brand", cascade = CascadeType.ALL, fetch = FetchType.LAZY,  orphanRemoval = true)
    private List<Product> products = new ArrayList<>();

    public void addProduct(Product product) {
        products.add(product);
        product.setBrand(this);
    }

    public void removeProduct(Product product) {
        products.remove(product);
        product.setBrand(null);
    }
}
