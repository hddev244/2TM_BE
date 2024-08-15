package store.chikendev._2tm.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import store.chikendev._2tm.entity.ShippingCost;

@Repository
public interface ShippingCostRepository extends JpaRepository<ShippingCost, Long> {
    @Query("SELECT s FROM ShippingCost s WHERE s.wardIdStore = :wardIdStore AND s.wardIdDelivery = :wardIdDelivery")
    Optional<ShippingCost> findByWardIds(@Param("wardIdStore") String wardIdStore, @Param("wardIdDelivery") String wardIdDelivery);
}
