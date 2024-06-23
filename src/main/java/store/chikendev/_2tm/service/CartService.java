package store.chikendev._2tm.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import store.chikendev._2tm.dto.request.CartItemRequest;
import store.chikendev._2tm.entity.CartItems;
import store.chikendev._2tm.entity.Product;
import store.chikendev._2tm.exception.AppException;
import store.chikendev._2tm.exception.ErrorCode;
import store.chikendev._2tm.entity.Account;
import store.chikendev._2tm.repository.CartItemsRepository;
import store.chikendev._2tm.repository.ProductRepository;
import store.chikendev._2tm.repository.AccountRepository;

@Service
public class CartService {

    @Autowired
    private CartItemsRepository cartItemsRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private AccountRepository accountRepository;

    public CartItems addProductToCart(CartItemRequest request) {
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Sản phẩm không tồn tại"));

    String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Account account = accountRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        CartItems cartItem = new CartItems();
        cartItem.setProduct(product);
        cartItem.setAccount(account);
        cartItem.setQuantity(request.getQuantity());

        return cartItemsRepository.save(cartItem);
    }
}
