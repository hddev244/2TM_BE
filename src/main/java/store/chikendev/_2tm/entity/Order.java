package store.chikendev._2tm.entity;

import java.util.Date;
import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double deliveryCost;
    private String note;

    @CreationTimestamp
    @Temporal(TemporalType.DATE)
    @Column(updatable = false)
    private Date createdAt;

    @Temporal(TemporalType.DATE)
    private Date completeAt;

    private Boolean paymentStatus;
    private String paymentId;
    private String consigneeDetailAddress;
    @Column(length = 70)
    private String consigneeName;
    @Column(length = 10)
    private String consigneePhoneNumber;
    private Double totalPrice;

    @ManyToOne
    @JoinColumn(name = "ordererId")
    private Account account;

    @ManyToOne
    @JoinColumn(name = "orderStateId")
    private StateOrder stateOrder;

    @ManyToOne
    @JoinColumn(name = "paymentMethodId")
    private PaymentMethods paymentMethod;

    @ManyToOne
    @JoinColumn(name = "consigneeWard")
    private Ward ward;

    @OneToOne(mappedBy = "order")
    private BillOfLading billOfLading;

    @OneToOne(mappedBy = "order")
    private ViolationRecord record;

}
