package store.chikendev._2tm.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import store.chikendev._2tm.entity.BillOfLading;

@Repository
public interface BillOfLadingRepository extends JpaRepository<BillOfLading, Long> {
    @Query("select o from BillOfLading o where o.deliveryPerson.id = ?1")
    List<BillOfLading> getBillOfLadingByDeliveryPersonId(String id);
}
