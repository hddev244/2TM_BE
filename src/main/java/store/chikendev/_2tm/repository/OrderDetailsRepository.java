package store.chikendev._2tm.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import store.chikendev._2tm.entity.Account;
import store.chikendev._2tm.entity.OrderDetails;

@Repository
public interface OrderDetailsRepository extends JpaRepository<OrderDetails, Long> {
    @Query("SELECT o FROM OrderDetails o WHERE o.product.ownerId =:account")
    Page<OrderDetails> findByStateId(@Param("account") Account account, Pageable pageable);

    List<OrderDetails> findByOrderId(Long orderId);

}
