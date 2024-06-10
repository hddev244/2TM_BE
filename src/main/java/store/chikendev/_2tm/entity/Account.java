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
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Builder
@Table(name = "accounts", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "username", "email", "phoneNumber" })
})
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    @Column(length = 50)
    private String username;
    @Column(length = 300)
    private String password;
    @Column(length = 50)
    private String fullName;
    @Column(length = 10)
    private String phoneNumber;
    @Column(length = 100)
    private String email;

    private Integer violationPoints;

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

    @OneToMany(mappedBy = "account")
    private List<RoleAccount> roles;

    @OneToMany(mappedBy = "account")
    private List<Address> addresses;

    @OneToOne(mappedBy = "account")
    private Otp otp;

    @OneToMany(mappedBy = "account")
    private AccountStore store;

    @OneToMany(mappedBy = "account")
    private List<Product> products;

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

}
