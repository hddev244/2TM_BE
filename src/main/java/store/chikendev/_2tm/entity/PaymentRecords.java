package store.chikendev._2tm.entity;

import java.util.Date;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
public class PaymentRecords {
    @Id
    @Column(length = 10)
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

     @CreationTimestamp
    @Column(updatable = false)
    private Date createdAt;

    @UpdateTimestamp
    private Date updatedAt;

    @OneToMany(mappedBy = "paymentRecord")
    private List<Order> orders;

}
