package store.chikendev._2tm.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import store.chikendev._2tm.entity.Account;
import store.chikendev._2tm.entity.ConsignmentOrders;
import store.chikendev._2tm.entity.StateConsignmentOrder;
import store.chikendev._2tm.entity.Store;

@Repository
public interface ConsignmentOrdersRepository extends JpaRepository<ConsignmentOrders, Long> {

        @Query("SELECT co FROM ConsignmentOrders co WHERE co.deliveryPerson = :deliveryPerson AND co.stateId = :state AND  (co.product.state.id = 4 or co.product.state.id = 7 or co.product.state.id = 8 or co.product.state.id = 3)")
        Page<ConsignmentOrders> findByDeliveryPersonAndStateId(@Param("deliveryPerson") Account deliveryPerson,
                        @Param("state") StateConsignmentOrder state, Pageable pageable);

        @Query("SELECT co FROM ConsignmentOrders co WHERE co.ordererId = :ordererId AND co.stateId = :state")
        Page<ConsignmentOrders> findByOrdererIdAndStateId(@Param("ordererId") Account ordererId,
                        @Param("state") StateConsignmentOrder state, Pageable pageable);

        @Query("SELECT co FROM ConsignmentOrders co WHERE co.store = :store AND co.stateId = :state")
        Page<ConsignmentOrders> findByStoreAndStateId(@Param("store") Store store,
                        @Param("state") StateConsignmentOrder state, Pageable pageable);

        @Query("SELECT co FROM ConsignmentOrders co WHERE co.product.ownerId = :owner AND co.stateId = :state")
        Page<ConsignmentOrders> getByStateOrAllWithOwner(@Param("owner") Account account,
                        @Param("state") StateConsignmentOrder state, Pageable pageable);

        @Query("SELECT co FROM ConsignmentOrders co WHERE co.product.ownerId = :owner")
        Page<ConsignmentOrders> getByStateOrAllWithOwner(@Param("owner") Account account, Pageable pageable);

        @Query("SELECT co FROM ConsignmentOrders co WHERE co.product.ownerId = :owner AND (co.product.state.id = 4 or co.product.state.id = 7 or co.product.state.id = 8 or co.product.state.id = 3)")
        Page<ConsignmentOrders> findByDeliveryPerson2(@Param("owner") Account deliveryPerson, Pageable pageable);

        Page<ConsignmentOrders> findByStore(Store store, Pageable pageable);

        Page<ConsignmentOrders> findByOrdererId(Account ordererId, Pageable pageable);

        @Query("SELECT co FROM ConsignmentOrders co WHERE co.store = :store AND co.stateId = :state AND co.product.state.id = 8")
        Page<ConsignmentOrders> findByStoreAndStateIsWatingStaffReceive(Store store, StateConsignmentOrder state,
                Pageable pageable);

}
