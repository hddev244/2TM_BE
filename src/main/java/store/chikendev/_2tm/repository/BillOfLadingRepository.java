package store.chikendev._2tm.repository;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import store.chikendev._2tm.entity.Account;
import store.chikendev._2tm.entity.BillOfLading;
import store.chikendev._2tm.entity.StateOrder;

@Repository
public interface BillOfLadingRepository
    extends JpaRepository<BillOfLading, Long> {
    @Query("select o from BillOfLading o where o.deliveryPerson.id = ?1")
    List<BillOfLading> getBillOfLadingByDeliveryPersonId(String id);

    @Query(
        "SELECT o FROM BillOfLading o WHERE o.deliveryPerson = :deliveryPerson AND o.order.stateOrder = :state"
    )
    Page<BillOfLading> getByDaliveryPersonIdAndStateId(
        @Param("deliveryPerson") Account deliveryPerson,
        @Param("state") StateOrder state,
        Pageable pageable
    );

    @Query(
        "SELECT co FROM BillOfLading co WHERE co.deliveryPerson = :deliveryPerson"
    )
    Page<BillOfLading> findByDeliveryPerson(
        @Param("deliveryPerson") Account deliveryPerson,
        Pageable pageable
    );
}
