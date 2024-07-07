package store.chikendev._2tm.entity;

import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Builder
public class AttributeCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "categoryId")
    private Category category;

    @ManyToMany
    @JoinTable(
            name = "attribute_category_values",
            joinColumns = @JoinColumn(name = "attributeCategoryId"),
            inverseJoinColumns = @JoinColumn(name = "attributeDetailId")
    )
    private List<AttributeDetail> attributeDetails;

    @ManyToOne
    @JoinColumn(name = "attributeId")
    private ProductAttributes attribute;

}
