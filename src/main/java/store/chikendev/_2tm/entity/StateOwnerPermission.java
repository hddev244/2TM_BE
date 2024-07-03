package store.chikendev._2tm.entity;

import java.util.List;

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
@Table(name = "StateOwnerPermission")
@Builder
public class StateOwnerPermission {
    public static final Long IN_CONFIRM = 1L;
    public static final Long CONFIRM = 2L;
    public static final Long REFUSE = 3L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String description;

    @OneToMany(mappedBy = "state")
    private List<OwnerPermission> ownerPermissions;
}
