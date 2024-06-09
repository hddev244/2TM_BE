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
    @Id
    @Column(length = 20)
    private String id;
    private String name;

    @OneToMany(mappedBy = "role")
    private List<Role> roles;

}
