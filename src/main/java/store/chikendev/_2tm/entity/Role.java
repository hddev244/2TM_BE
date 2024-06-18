package store.chikendev._2tm.entity;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "roles")
@Builder
public class Role {
    public static final String ROLE_PRODUCT_OWNER = "CH";
    public static final String ROLE_CUSTOMER = "KH";
    public static final String ROLE_ADMIN = "QTV";
    public static final String ROLE_SHIPPER = "NVGH";
    public static final String ROLE_USER = "ND";
    public static final String ROLE_STAFF = "NVCH";
    public static final String ROLE_STORE_MANAGER = "QLCH";

    @Id
    @Column(length = 20, nullable = false)
    private String id;
    private String name;

    @OneToMany(mappedBy = "role")
    private List<RoleAccount> roles;

}
