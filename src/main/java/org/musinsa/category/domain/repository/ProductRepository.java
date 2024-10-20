package org.musinsa.category.domain.repository;

import java.util.List;
import org.musinsa.category.domain.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query(value = "SELECT p.category, b.name, p.price " +
            "FROM product p " +
            "JOIN brand b ON p.brand_id = b.id " +
            "WHERE (p.category, p.price) IN (" +
            "    SELECT category, MIN(price) " +
            "    FROM product " +
            "    WHERE DEL_DT IS NULL " +
            "    GROUP BY category" +
            ") " +
            "AND (p.category, b.name) IN (" +
            "    SELECT p2.category, MAX(b2.name) " +  // Changed MIN to MAX
            "    FROM product p2 " +
            "    JOIN brand b2 ON p2.brand_id = b2.id " +
            "    WHERE (p2.category, p2.price) IN (" +
            "        SELECT category, MIN(price) " +
            "        FROM product " +
            "        WHERE DEL_DT IS NULL " +
            "        GROUP BY category" +
            "    ) " +
            "    AND p2.DEL_DT IS NULL " +
            "    GROUP BY p2.category" +
            ") " +
            "AND p.DEL_DT IS NULL " +
            "ORDER BY p.category",
            nativeQuery = true)
    List<Object[]> findLowestPriceByCategory();

    @Query("SELECT p FROM Product p WHERE p.category = :category "
            + "AND p.price = (SELECT MIN(p2.price) FROM Product p2 WHERE p2.category = :category AND p2.deletedDate IS NULL) "
            + "AND p.deletedDate IS NULL")
    List<Product> findLowestPriceProductsByCategory(@Param("category") String category);


    @Query("SELECT p FROM Product p WHERE p.category = :category "
            + "AND p.price = (SELECT MAX(p2.price) FROM Product p2 WHERE p2.category = :category AND p2.deletedDate IS NULL) "
            + "AND p.deletedDate IS NULL")
    List<Product> findHighestPriceProductsByCategory(@Param("category") String category);


    @Query("SELECT b.name as brandName, SUM(p.price) as totalPrice " +
            "FROM Brand b JOIN b.products p " +
            "WHERE p.deletedDate IS NULL AND b.deletedDate IS NULL " +
            "GROUP BY b.id " +
            "ORDER BY totalPrice ASC")
    List<Object[]> findBrandWithLowestTotalPrice();

    List<Product> findByBrandNameAndDeletedDateIsNull(String brandName);

}
