package store.chikendev._2tm.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import store.chikendev._2tm.entity.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {
        List<Product> findByQuantityGreaterThanOrderByCreatedAtDesc(int quantity);
    
}
