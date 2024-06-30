package store.chikendev._2tm.entity;

import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
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
@Table(name = "images", uniqueConstraints = {
        @UniqueConstraint(columnNames = "url")
})
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fileName;
    private String fileDownloadUri;
    private String fileType;
    private String fileId;
    private long size;

    @OneToMany(mappedBy = "image")
    private List<Account> accounts;

    @OneToMany(mappedBy = "image")
    private List<Store> stores;

    @OneToMany(mappedBy = "image")
    private List<Category> categories;

    @OneToMany(mappedBy = "image")
    private List<ProductImages> products;

    @OneToMany(mappedBy = "image")
    private List<BillOfLading> billOfLadings;
}
