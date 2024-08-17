package store.chikendev._2tm.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import store.chikendev._2tm.entity.Product;
import store.chikendev._2tm.entity.ProductImages;

@Repository
public interface ProductImagesRepository extends JpaRepository<ProductImages, Long> {
    List<ProductImages> findByProduct(Product product);
}
