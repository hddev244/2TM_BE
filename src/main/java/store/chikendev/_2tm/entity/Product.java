package store.chikendev._2tm.entity;

import java.util.Date;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.CascadeType;
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
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Builder
@Table(name = "products")
public class Product {
    public static final Boolean TYPE_PRODUCT_OF_STORE = true;
    public static final Boolean TYPE_PRODUCT_OF_ACCOUNT = false;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(length = 100)
    private String name;
    private Double price;
    private Integer quantity;
    @Column(length = 500, columnDefinition = "NVARCHAR(500)")
    private String description;

    @CreationTimestamp
    @Column(updatable = false)
    private Date createdAt;

    @UpdateTimestamp
    private Date updatedAt;

    private Boolean type;

    @ManyToOne
    @JoinColumn(name = "ownerId")
    private Account ownerId;

    @ManyToOne
    @JoinColumn(name = "accountId")
    private Account account;

    @ManyToOne
    @JoinColumn(name = "categoryId")
    private Category category;

    @ManyToOne
    @JoinColumn(name = "stateId")
    private StateProduct state;

    @ManyToOne
    @JoinColumn(name = "storeId")
    private Store store;

    @ManyToOne
    @JoinColumn(name = "productCommission")
    private ProductCommission productCommission;

    @OneToMany(mappedBy = "product")
    private List<ProductAttributeDetail> attributes;

    @OneToMany(mappedBy = "product")
    private List<ProductImages> images;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "consignment_id", referencedColumnName = "id")
    private ConsignmentOrders consignmentOrder;

    @OneToMany(mappedBy = "product")
    private List<CartItems> cartItems;

}
