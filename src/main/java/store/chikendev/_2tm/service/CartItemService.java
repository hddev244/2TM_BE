package store.chikendev._2tm.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import store.chikendev._2tm.dto.request.CartItemRequest;
import store.chikendev._2tm.dto.responce.AttributeProductResponse;
import store.chikendev._2tm.dto.responce.CartItemResponse;
import store.chikendev._2tm.dto.responce.ProductResponse;
import store.chikendev._2tm.dto.responce.StoreResponse;
import store.chikendev._2tm.entity.Account;
import store.chikendev._2tm.entity.CartItems;
import store.chikendev._2tm.entity.Product;
import store.chikendev._2tm.entity.ProductAttributeDetail;
import store.chikendev._2tm.entity.Store;
import store.chikendev._2tm.exception.AppException;
import store.chikendev._2tm.exception.ErrorCode;
import store.chikendev._2tm.repository.AccountRepository;
import store.chikendev._2tm.repository.CartItemsRepository;
import store.chikendev._2tm.repository.ProductRepository;
import store.chikendev._2tm.utils.EntityFileType;
import store.chikendev._2tm.utils.FilesHelp;

@Service
public class CartItemService {

        @Autowired
        private CartItemsRepository cartRepository;

        @Autowired
        private AccountRepository accountRepository;

        @Autowired
        private ProductRepository productRepository;

        @Autowired
        private ProductService productService;

        // Lấy tìm userid qua token
        public List<CartItemResponse> getUserCart() {
                String email = SecurityContextHolder.getContext().getAuthentication().getName();
                Account account = accountRepository.findByEmail(email)
                                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
                List<CartItems> carts = cartRepository.getItemsByAccount(account.getId());
                return carts.stream().map(this::convertToCartDto).collect(Collectors.toList());
        }

        // Lấy thông tin giỏ hàng
        private CartItemResponse convertToCartDto(CartItems cart) {
                return CartItemResponse.builder()
                                .id(cart.getId())
                                .quantity(cart.getQuantity())
                                .product(convertToProductDto(cart.getProduct()))
                                .build();

        }

        // Lấy Store
        private String getStoreAddress(Store store) {
                if (store == null) {
                        return "";
                }
                if (store.getWard() != null) {
                        String StoreWard = store.getWard().getName();
                        String StoreDistrict = store.getWard().getDistrict().getName();
                        String StoreProvince = store.getWard().getDistrict().getProvinceCity().getName();
                        String storeAddress = store.getStreetAddress() == null ? "" : store.getStreetAddress() + ", ";
                        return storeAddress + StoreWard + ", " + StoreDistrict + ", " + StoreProvince;
                }
                return "";
        }

        // Lấy sản phẩm
        private List<ProductResponse> convertToProductDto(Product product) {
                List<ProductResponse> productResponses = new ArrayList<>();
                if (product != null) {
                        productResponses.add(ProductResponse.builder()
                                        .id(product.getId())
                                        .name(product.getName())
                                        .price(product.getPrice())
                                        .quantity(product.getQuantity())
                                        .description(product.getDescription())
                                        .store(StoreResponse.builder()
                                                        .id(product.getStore().getId())
                                                        .name(product.getStore().getName())
                                                        .streetAddress(getStoreAddress(product.getStore()))
                                                        .build())
                                        .build());
                }
                return productResponses;
        }

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

        return cartRepository.save(cartItem);
    }
}

