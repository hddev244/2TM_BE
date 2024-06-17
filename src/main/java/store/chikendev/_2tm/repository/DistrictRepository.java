package store.chikendev._2tm.repository;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import store.chikendev._2tm.entity.District;

@Repository
public interface DistrictRepository extends JpaRepository<District, Long> {
    @Query("SELECT o FROM District o WHERE o.provinceCity.id = ?1")
    List<District> getDistrictByProvinceId(Long id);
}
