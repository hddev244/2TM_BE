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
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
public class OwnerPermission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreationTimestamp
    @Column(updatable = false)
    private Date createdAt;

    @Column(length = 100)
    private String bankName;

    @Column(length = 100)
    private String BankAccountNumber;

    @Column(length = 100)
    private String accountHolderName;

    @ManyToOne
    @JoinColumn(name = "stateId")
    private StateOwnerPermission state;

    @OneToOne
    @JoinColumn(name = "accountId")
    private Account account;
}
