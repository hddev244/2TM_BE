package store.chikendev._2tm.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import store.chikendev._2tm.entity.ProductCommission;

public interface ProductCommissionRepository extends JpaRepository<ProductCommission, Long> {
    @Query("SELECT p FROM ProductCommission p ORDER BY p.createdAt DESC")
    List<ProductCommission> findAllOrderByCreatedAtDesc();
}
