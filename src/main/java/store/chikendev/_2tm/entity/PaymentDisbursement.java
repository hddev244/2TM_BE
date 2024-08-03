package store.chikendev._2tm.entity;

import java.util.Date;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Builder
public class PaymentDisbursement {
    @Id
    @Column(length = 10)
    private String id;
    private Double amount;
    @CreationTimestamp
    @Column(updatable = false)
    private Date payDate;
    private Boolean status;
    @Column(length = 50)
    private String bankTranNo;

    @Column(length = 100)
    private String bankNameOwner;

    @Column(length = 100)
    private String BankAccountNumberOwner;

    @Column(length = 100)
    private String accountHolderNameOwner;

    @Column(length = 100)
    private String bankNameAdmin;

    @Column(length = 100)
    private String BankAccountNumberAdmin;

    @Column(length = 100)
    private String accountHolderNameAdmin;

    @OneToMany(mappedBy = "paymentDisbursement")
    private List<Disbursements> Disbursements;

}