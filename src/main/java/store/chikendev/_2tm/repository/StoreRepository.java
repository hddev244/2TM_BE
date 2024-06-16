package store.chikendev._2tm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import store.chikendev._2tm.entity.Store;

public interface StoreRepository extends JpaRepository<Store, Long> {
}
