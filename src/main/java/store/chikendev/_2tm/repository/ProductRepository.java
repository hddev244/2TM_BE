package store.chikendev._2tm.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import store.chikendev._2tm.entity.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {
    List<Product> findByQuantityGreaterThanOrderByCreatedAtDesc(int quantity);

    @Query("select p from Product p where p.name like %:searchTerm% or p.description like %:searchTerm%")
    Page<Product> findProductsBySearchTerm(@Param("searchTerm") String searchTerm, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.quantity > 0 ORDER BY p.createdAt DESC")
    Page<Product> findAvailableProducts(Pageable pageable);
}
