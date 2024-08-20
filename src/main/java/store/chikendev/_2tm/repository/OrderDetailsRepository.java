package store.chikendev._2tm.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.access.method.P;
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
                        @Param("endOfDay") LocalDateTime endOfDay, @Param("account") Account account,
                        Pageable pageable);

        @Query("SELECT e FROM OrderDetails e WHERE (e.order.completeAt >= :startOfMonth AND e.order.completeAt < :endOfMonth) AND e.product.ownerId =:account")
        Page<OrderDetails> findAllByMonth(@Param("startOfMonth") LocalDateTime startOfMonth,
                        @Param("endOfMonth") LocalDateTime endOfMonth,
                        @Param("account") Account account, Pageable pageable);

                        @Query(value = "SELECT o.product_id AS productId, SUM(o.quantity) AS totalQuantity, SUM(o.price * o.quantity) AS totalRevenue " +
                        "FROM order_details o " +
                        "JOIN orders ord ON o.order_id = ord.id " +
                        "JOIN products p ON o.product_id = p.id " +
                        "WHERE ord.complete_at >= :startOfDay AND ord.complete_at < :endOfDay " +
                        "AND (p.type = :proructType OR :proructType IS NULL ) " +
                        "AND (:storeId IS NULL OR p.store_id = :storeId) " +
                        "GROUP BY o.product_id " +
                        "ORDER BY totalQuantity DESC "+
                        "LIMIT 10"
                        , nativeQuery = true) 
        List<Object[]> getProductReportQtyAndTotalPrice(
                        @Param("startOfDay") LocalDate startOfDay,
                        @Param("endOfDay") LocalDate endOfDay,
                        @Param("proructType") Boolean proructType,
                        @Param("storeId") Long storeId);

        // @Query(value = "SELECT DATE(o.complete_at), SUM(o.total_price),COUNT(o.id) "
        // +
        // "FROM OrderDetails o " +
        // "WHERE o.complete_at >= :startOfDay AND o.complete_at < :endOfDay AND
        // o.store_id = :store_id " +
        // "GROUP BY DATE(o.complete_at)" +
        // "ORDER BY DATE(o.complete_at) DESC", nativeQuery = true)
        // List<Object[]> getNativeReportByRangeDateAndStore(@Param("startOfDay")
        // LocalDate startOfDay,
        // @Param("endOfDay") LocalDate endOfDay, @Param("store_id") Long storeId);

}
