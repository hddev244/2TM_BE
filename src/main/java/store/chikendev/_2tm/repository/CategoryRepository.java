package store.chikendev._2tm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import store.chikendev._2tm.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
