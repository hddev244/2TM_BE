package store.chikendev._2tm.entity;

import java.util.Date;
import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
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
@Table(name = "otps")
public class Otp {

    @Id
    @Column(length = 6)
    private String tokenCode;

    @CreationTimestamp
    @Temporal(TemporalType.DATE)
    @Column(updatable = false)
    private Date createdAt;

    @Column(length = 10, unique = true, name = "phoneNumber")
    private String phoneNumber;

    @OneToOne
    @JoinColumn(name = "account_phone_number", referencedColumnName = "phoneNumber", unique = true, insertable = false, updatable = false)
    private Account account;
}