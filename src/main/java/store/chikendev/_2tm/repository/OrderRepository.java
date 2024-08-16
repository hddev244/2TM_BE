package store.chikendev._2tm.repository;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import store.chikendev._2tm.entity.Account;
import store.chikendev._2tm.entity.Order;
import store.chikendev._2tm.entity.OrderDetails;
import store.chikendev._2tm.entity.PaymentRecords;
import store.chikendev._2tm.entity.StateOrder;
import store.chikendev._2tm.entity.Store;
import store.chikendev._2tm.entity.Voucher;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByPaymentRecord(PaymentRecords paymentRecord);

    Page<Order> findByAccountId(String id, Pageable pageable);

    Page<Order> findByAccountIdAndStateOrderId(String id, Long statusId, Pageable pageable);

    Page<Order> findByStore(Store store, Pageable pageable);

    @Query("SELECT o FROM Order o WHERE o.store = :store AND o.stateOrder = :state ")
    Page<Order> findByStoreAndStateId(@Param("store") Store store,
            @Param("state") StateOrder state, Pageable pageable);

    @Query("SELECT o FROM Order o WHERE o.account = :account AND o.stateOrder = :state and o.type = true AND (o.paymentStatus = true or (o.paymentStatus = false AND o.paymentMethod.id = 1))")
    Page<Order> findByAccountIdAndStateId(@Param("account") Account account,
            @Param("state") StateOrder state, Pageable pageable);

    @Query("SELECT o FROM Order o WHERE o.account = :account and o.type = true AND (o.paymentStatus = true or (o.paymentStatus = false AND o.paymentMethod.id = 1)) ")
    Page<Order> findByAccountIdAndType(@Param("account") Account account, Pageable pageable);

    @Query("SELECT o FROM Order o WHERE o.id IN :orderIds")
    Page<Order> findByIdIn(@Param("orderIds") List<Long> orderIds, Pageable pageable);

    @Query("SELECT o FROM Order o WHERE o.voucher = :voucher")
    List<Order> findByVoucher(@Param("voucher") Voucher voucher);

    @Query("SELECT o FROM Order o WHERE o.completeAt = :completeAt")
    Page<Order> findByCompleteAt(@Param("completeAt") Date completeAt, Pageable pageable);

    @Query("SELECT e FROM Order e WHERE (e.completeAt >= :startOfDay AND e.completeAt < :endOfDay) AND e.store = :store AND e.type = :type")
    Page<Order> findByDateAndType(@Param("startOfDay") LocalDateTime startOfDay,
            @Param("endOfDay") LocalDateTime endOfDay, @Param("store") Store store, @Param("type") Boolean type,
            Pageable pageable);

    @Query("SELECT e FROM Order e WHERE (e.completeAt >= :startOfDay AND e.completeAt < :endOfDay) AND e.store = :store")
    Page<Order> findByDate(@Param("startOfDay") LocalDateTime startOfDay,
            @Param("endOfDay") LocalDateTime endOfDay, @Param("store") Store store, Pageable pageable);

    @Query("SELECT e FROM Order e WHERE (e.completeAt >= :startOfMonth AND e.completeAt < :endOfMonth) AND e.store = :store")
    Page<OrderDetails> findAllByMonth(@Param("startOfMonth") LocalDateTime startOfMonth,
            @Param("endOfMonth") LocalDateTime endOfMonth,
            @Param("store") Store store, Pageable pageable);

    @Query("SELECT e FROM Order e WHERE (e.completeAt >= :startOfMonth AND e.completeAt < :endOfMonth) AND e.store = :store AND e.type = :type")
    Page<OrderDetails> findAllByMonthAndType(@Param("startOfMonth") LocalDateTime startOfMonth,
            @Param("endOfMonth") LocalDateTime endOfMonth,
            @Param("store") Store store,
            @Param("type") Boolean type, Pageable pageable);

}
