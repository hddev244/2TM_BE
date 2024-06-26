package store.chikendev._2tm.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import store.chikendev._2tm.entity.CartItems;

@Repository
public interface CartItemsRepository extends JpaRepository<CartItems, Long> {
    @Query("SELECT o FROM CartItems o WHERE o.account.id = ?1")
    List<CartItems> getItemsByAccount(String Id);

    @Query("SELECT o FROM CartItems o WHERE o.product.id =?1")
    List<CartItems> getItemsByProduct(Long Id);

    CartItems findCartItemsByAccountIdAndProductId(String id, Long idProduct);
}
