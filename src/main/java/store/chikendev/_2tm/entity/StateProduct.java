package store.chikendev._2tm.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "statesProduct")
@Builder
public class StateProduct {

    public static final Long IN_CONFIRM = 1L;
    public static final Long CONFIRM = 2L;
    public static final Long REFUSE = 3L;
    public static final Long DELYVERING = 4L;
    public static final Long CANCELED = 6L;
    public static final Long WAITING_PICK_UP = 7L;
    public static final Long WAITING_STAFF_RECEIVE = 8L; // Chờ cửa hàng nhận hàng

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String description;

    @JsonIgnore
    @OneToMany(mappedBy = "state")
    private List<Product> products;
}
