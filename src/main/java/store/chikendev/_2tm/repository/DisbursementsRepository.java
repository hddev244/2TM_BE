package store.chikendev._2tm.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import store.chikendev._2tm.entity.Disbursements;

@Repository
public interface DisbursementsRepository extends JpaRepository<Disbursements, Long> {
    @Query("SELECT d.orderDetail.id FROM Disbursements d WHERE d.paymentClerk.id = :accountId AND d.state = :state")
    List<Long> findOrderIdsByPaymentClerkAndState(@Param("accountId") String accountId, @Param("state") Boolean state);

    @Query("SELECT d FROM Disbursements d WHERE d.orderDetail.product.ownerId.id = :accountId AND d.state = :state")
    Page<Disbursements> findDisbursementsByOwnerIdAndState(@Param("accountId") String accountId,
            @Param("state") Boolean state, Pageable pageable);

    @Query("SELECT d FROM Disbursements d WHERE d.orderDetail.product.ownerId.id = :accountId")
    Page<Disbursements> findDisbursementsByOwnerId(@Param("accountId") String accountId, Pageable pageable);
}
