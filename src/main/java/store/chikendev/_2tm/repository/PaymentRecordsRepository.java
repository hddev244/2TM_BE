package store.chikendev._2tm.repository;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import store.chikendev._2tm.entity.Account;
import store.chikendev._2tm.entity.PaymentRecords;

@Repository
public interface PaymentRecordsRepository
    extends JpaRepository<PaymentRecords, String> {
    @Query(
        "SELECT co FROM PaymentRecords co WHERE co.account = :account AND co.status = :status"
    )
    Page<PaymentRecords> findByAccountAndStatus(
        @Param("account") Account account,
        @Param("status") Boolean status,
        Pageable pageable
    );

    Page<PaymentRecords> findByAccount(Account account, Pageable pageable);

    @Query(
        "SELECT pr FROM PaymentRecords pr JOIN pr.orders o WHERE o.type = true AND o.account = :account AND o.paymentStatus = false"
    )
    Page<PaymentRecords> getAllNotPaidYet(
        @Param("account") Account account,
        Pageable pageable
    );
}
