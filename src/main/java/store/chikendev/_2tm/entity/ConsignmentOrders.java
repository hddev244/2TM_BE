package store.chikendev._2tm.entity;

import java.util.Date;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Builder
public class ConsignmentOrders {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreationTimestamp
    @Column(updatable = false)
    private Date createdAt;

    @Temporal(TemporalType.TIMESTAMP)
    private Date completedAt;

    private String note;

    private String phoneNumber;

    private String detailAddress;

    @Temporal(TemporalType.TIMESTAMP)
    private Date statusChangeDate;

    @ManyToOne
    @JoinColumn(name = "ordererId")
    private Account ordererId;

    @OneToOne
    @JoinTable(name = "consignment_orders_image", joinColumns = @JoinColumn(name = "consignmentOrderId"), inverseJoinColumns = @JoinColumn(name = "imageId"))
    private Image image;

    @ManyToOne
    @JoinColumn(name = "deliveryPerson")
    private Account deliveryPerson;

    @ManyToOne
    @JoinColumn(name = "productId")
    private Product product;

    @ManyToOne
    @JoinColumn(name = "storeId")
    private Store store;

    @ManyToOne
    @JoinColumn(name = "stateId")
    private StateConsignmentOrder stateId;

    @ManyToOne
    @JoinColumn(name = "wardId")
    private Ward ward;

}
