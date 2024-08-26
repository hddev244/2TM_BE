package store.chikendev._2tm.repository;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import store.chikendev._2tm.entity.Category;
import store.chikendev._2tm.entity.Product;
import store.chikendev._2tm.entity.ProductCommission;
import store.chikendev._2tm.entity.Store;

@Repository
public interface ProductRepository
    extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {
    List<Product> findByQuantityGreaterThanOrderByCreatedAtDesc(int quantity);

    // @Query("select p from Product p where p.name like %:searchTerm% or
    // p.description like %:searchTerm%")
    // Page<Product> findProductsBySearchTerm(@Param("searchTerm") String
    // searchTerm, Pageable pageable);

    // lấy sản phẩm đang bán để show lên trang chủ -
    // ** nếu laấy để làm chức năng khác thì tạo cái riêng
    // không thêm chức nang khác vào đâyủ
    @Query(
        "SELECT p FROM Product p WHERE p.quantity > 0 AND p.state.id = 2 ORDER BY p.createdAt DESC"
    )
    Page<Product> findAvailableProducts(Pageable pageable);

    @Query(
        "SELECT p FROM Product p WHERE p.ownerId.id = :ownerId AND (:stateId IS NULL OR p.state.id = :stateId)"
    )
    Page<Product> findConsignmentProductsByOwnerIdAndState(
        @Param("ownerId") String ownerId,
        @Param("stateId") Long stateId,
        Pageable pageable
    );

    @Query(
        "SELECT p FROM Product p WHERE p.quantity > 0 AND p.state.id = 2 AND (p.name LIKE :value OR p.description LIKE :value)"
    )
    Page<Product> findProductsBySearchTerm(
        @Param("value") String value,
        Pageable pageable
    );

    @Query(
        "SELECT p FROM Product p WHERE p.store = :store AND (:stateProduct IS NULL OR p.state.id = :stateProduct) AND p.type = :type"
    )
    Page<Product> findConsignmentProductsByStoreAndState(
        @Param("store") Store store,
        @Param("stateProduct") Long stateProductId,
        @Param("type") Boolean type,
        Pageable pageable
    );

    @Query(
        "SELECT p FROM Product p WHERE p.quantity > 0 AND p.category = :category AND p.state.id = 2"
    )
    Page<Product> findByPathCategory(
        @Param("category") Category category,
        Pageable pageable
    );

    // nv xem san pham theo store
    @Query(
        "SELECT p FROM Product p WHERE p.store = :store  ORDER BY p.createdAt DESC"
    )
    Page<Product> findAllProductsInStore(
        @Param("store") Store store,
        Pageable pageable
    );

    // kh xem san pham theo store và chi tiết store
    @Query(
        "SELECT p FROM Product p WHERE p.quantity > 0 AND p.store = :store AND p.state.id = 2 ORDER BY p.createdAt DESC"
    )
    Page<Product> findAllProductsInStoreKH(
        @Param("store") Store store,
        Pageable pageable
    );

    @Query(
        "SELECT p FROM Product p WHERE p.quantity > 0 AND p.state.id = 2 AND (:categoryId IS NULL OR p.category.id = :categoryId) AND (:storeId IS NULL OR p.store.id = :storeId) AND (:minPrice IS NULL OR p.price >= :minPrice) AND (:maxPrice IS NULL OR p.price <= :maxPrice) ORDER BY p.createdAt DESC"
    )
    Page<Product> findProductByCondition(
        @Param("categoryId") Long categoryId,
        @Param("storeId") Long storeId,
        @Param("minPrice") Long minPrice,
        @Param("maxPrice") Long maxPrice,
        Pageable pageable
    );

    // CH xem danh sách ký gửi
    @Query(
        "SELECT p FROM Product p WHERE p.ownerId.id = :ownerId and p.state.id = 2"
    )
    Page<Product> findProductsByOwnerIdAndState(
        @Param("ownerId") String ownerId,
        Pageable pageable
    );

    @Transactional
    @Modifying
    @Query(
        "UPDATE Product p SET p.productCommission = :productCommission WHERE p.id = :productId"
    )
    void updateProductCommission(
        @Param("productCommission") ProductCommission productCommission,
        @Param("productId") Long productId
    );

    @Query(
        "SELECT p FROM Product p WHERE p.quantity > 0 AND p.state.id = 2 AND (p.name LIKE %:searchValue% OR p.description LIKE %:searchValue%) AND (:categoryPath IS NULL OR p.category.path LIKE :categoryPath) AND (:storeId IS NULL OR p.store.id = :storeId) AND (:minPrice IS NULL OR p.price >= :minPrice) AND (:maxPrice IS NULL OR p.price <= :maxPrice)"
    )
    Page<Product> searchAndFilterProducts(
        @Param("searchValue") String searchValue,
        @Param("categoryPath") String categoryPath,
        @Param("storeId") Long storeId,
        @Param("minPrice") Double minPrice,
        @Param("maxPrice") Double maxPrice,
        Pageable pageable
    );
}
