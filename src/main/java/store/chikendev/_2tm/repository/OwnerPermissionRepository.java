package store.chikendev._2tm.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import store.chikendev._2tm.entity.OwnerPermission;
import store.chikendev._2tm.entity.StateOwnerPermission;

@Repository
public interface OwnerPermissionRepository extends JpaRepository<OwnerPermission, Long> {
    @Query("SELECT co FROM OwnerPermission co WHERE co.state = :state")
    Page<OwnerPermission> findByStateId(@Param("state") StateOwnerPermission state, Pageable pageable);

    OwnerPermission findByAccountId(String id);

}
