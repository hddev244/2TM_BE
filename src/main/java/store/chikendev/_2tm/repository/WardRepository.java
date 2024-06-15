package store.chikendev._2tm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import store.chikendev._2tm.entity.Ward;

@Repository
public interface WardRepository extends JpaRepository<Ward, Long> {
    
}
