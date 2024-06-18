package store.chikendev._2tm.entity;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "statesAccount")
@Builder
public class StateAccount {
    public static final Long ACTIVE = 1L;
    public static final Long INACTIVE = 2L;
    public static final Long CLOSED = 3L;
    public static final Long VERIFICATION_REQUIRED = 4L;
    public static final Long LOCKED = 5L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(length = 50)
    private String name;

    @OneToMany(mappedBy = "state")
    private List<Account> accounts;

}
