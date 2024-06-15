package store.chikendev._2tm.entity;

import java.util.Date;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "accounts", indexes = {
        @Index(name = "idx_phone_number", columnList = "phoneNumber")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    @Column(length = 50, unique = true)
    private String username;
    @Column(length = 300)
    private String password;
    @Column(length = 100)
    private String fullName;
    private Integer violationPoints;
    @Column(length = 10, unique = true)
    private String phoneNumber;
    @Column(length = 100, unique = true)
    private String email;

    @CreationTimestamp
    @Temporal(TemporalType.DATE)
    @Column(updatable = false)
    private Date createdAt;

    @UpdateTimestamp
    @Temporal(TemporalType.DATE)
    private Date updatedAt;

    @ManyToOne
    @JoinColumn(name = "image_id")
    private Image image;

    @ManyToOne
    @JoinColumn(name = "state_id")
    private StateAccount state;

    @ManyToOne
    @JoinColumn(name = "primaryAddress")
    private Address address;

    @OneToMany(mappedBy = "account")
    private List<RoleAccount> roles;

    @OneToMany(mappedBy = "account")
    private List<Address> addresses;

    @OneToOne(mappedBy = "account")
    private Otp otp;

    @OneToOne(mappedBy = "account")
    private AccountStore store;

    @OneToMany(mappedBy = "account")
    private List<Product> products;

    @OneToMany(mappedBy = "ownerId")
    private List<Product> products_owner;

    @OneToMany(mappedBy = "paymentClerk")
    private List<Disbursements> disbursements;

    @OneToMany(mappedBy = "account")
    private List<Order> orders;

    @OneToMany(mappedBy = "deliveryPerson")
    private List<BillOfLading> deliveryPersons;

    @OneToMany(mappedBy = "createBy")
    private List<BillOfLading> createBys;

    @OneToMany(mappedBy = "account")
    private List<ViolationRecord> violationRecords;

    @OneToMany(mappedBy = "ordererId")
    private List<ConsignmentOrders> ordererConsignmentOrders;

    @OneToMany(mappedBy = "deliveryPerson")
    private List<ConsignmentOrders> deliveryPersonConsignmentOrders;

    @OneToMany(mappedBy = "account")
    private List<CartItems> CartItems;

}
