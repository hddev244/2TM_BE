package store.chikendev._2tm.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import store.chikendev._2tm.entity.Ward;

@Repository
public interface WardRepository extends JpaRepository<Ward, Long> {
    @Query("SELECT o FROM Ward o WHERE o.district.id = ?1")
    List<Ward> getByDistrictId(Long id);
}
