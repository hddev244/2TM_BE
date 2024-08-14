package store.chikendev._2tm.repository;

import java.time.LocalDateTime;
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

@Repository
public interface OrderDetailsRepository extends JpaRepository<OrderDetails, Long> {
    @Query("SELECT o FROM OrderDetails o WHERE o.product.ownerId =:account")
    Page<OrderDetails> findByStateId(@Param("account") Account account, Pageable pageable);

    List<OrderDetails> findByOrderId(Long orderId);

    List<OrderDetails> findByOrder(Order order);

    @Query("SELECT e FROM OrderDetails e WHERE (e.order.completeAt >= :startOfDay AND e.order.completeAt < :endOfDay) AND e.product.ownerId =:account")
    Page<OrderDetails> findAllByDate(@Param("startOfDay") LocalDateTime startOfDay,
            @Param("endOfDay") LocalDateTime endOfDay, @Param("account") Account account, Pageable pageable);

    @Query("SELECT e FROM OrderDetails e WHERE (e.order.completeAt >= :startOfMonth AND e.order.completeAt < :endOfMonth) AND e.product.ownerId =:account")
    Page<OrderDetails> findAllByMonth(@Param("startOfMonth") LocalDateTime startOfMonth,
            @Param("endOfMonth") LocalDateTime endOfMonth,
            @Param("account") Account account, Pageable pageable);

}
