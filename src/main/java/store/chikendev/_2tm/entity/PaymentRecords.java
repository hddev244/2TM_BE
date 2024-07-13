package store.chikendev._2tm.entity;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Builder
public class PaymentRecords {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private Double amount;
    @Column(length = 50)
    private String bankCode;
    @Column(length = 50)
    private String cartType;
    @Column(length = 50)
    private String bankTranNo;
    private Date payDate;
    private Boolean status;
    @ManyToOne
    @JoinColumn(name = "accountId")
    private Account account;

}
