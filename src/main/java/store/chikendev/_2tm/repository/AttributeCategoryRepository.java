package store.chikendev._2tm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import store.chikendev._2tm.entity.AttributeCategory;
import store.chikendev._2tm.entity.Category;

import java.util.List;

@Repository
public interface AttributeCategoryRepository extends JpaRepository<AttributeCategory, Long> {
    List<AttributeCategory> findByCategory(Category category);
}
