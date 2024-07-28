package store.chikendev._2tm.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import store.chikendev._2tm.entity.Account;
import store.chikendev._2tm.entity.Product;
import store.chikendev._2tm.entity.Store;

@Repository
public interface ProductRepository
    extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {
    List<Product> findByQuantityGreaterThanOrderByCreatedAtDesc(int quantity);

    // @Query("select p from Product p where p.name like %:searchTerm% or p.description like %:searchTerm%")
    // Page<Product> findProductsBySearchTerm(@Param("searchTerm") String searchTerm, Pageable pageable);

    @Query(
        "SELECT p FROM Product p WHERE p.quantity > 0 AND p.state.id = 2 ORDER BY p.createdAt DESC"
    )
    Page<Product> findAvailableProducts(Pageable pageable);

    @Query(
        "SELECT p FROM Product p WHERE p.quantity > 0 AND p.category.id = :categoryId ORDER BY p.createdAt DESC"
    )
    Page<Product> findAvailableProductsByCategory(
        @Param("categoryId") Long categoryId,
        Pageable pageable
    );

    @Query(
        "SELECT p FROM Product p WHERE p.ownerId.id = :ownerId AND (:stateId IS NULL OR p.state.id = :stateId)"
    )
    Page<Product> findConsignmentProductsByOwnerIdAndState(
        @Param("ownerId") String ownerId,
        @Param("stateId") Long stateId,
        Pageable pageable
    );

    @Query(
        "SELECT p FROM Product p WHERE p.quantity > 0 AND p.state.id = 2L AND (p.name LIKE :value OR p.description LIKE :value)"
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

    Page<Product> findByCategoryPath(String path, Pageable pageable);
}
