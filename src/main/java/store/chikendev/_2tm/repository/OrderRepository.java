package store.chikendev._2tm.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import store.chikendev._2tm.entity.Order;
import store.chikendev._2tm.entity.PaymentRecords;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByPaymentRecord(PaymentRecords paymentRecord);

    Page<Order> findByAccountId(String id, Pageable pageable);

}
