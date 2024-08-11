package store.chikendev._2tm.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import store.chikendev._2tm.entity.ProductCommission;

public interface ProductCommissionRepository extends JpaRepository<ProductCommission, Long> {
    
}
