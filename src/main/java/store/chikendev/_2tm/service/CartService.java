package store.chikendev._2tm.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import store.chikendev._2tm.dto.responce.AttributeProductResponse;
import store.chikendev._2tm.dto.responce.CartResponse;
import store.chikendev._2tm.dto.responce.ProductResponse;
import store.chikendev._2tm.dto.responce.ResponseDocumentDto;
import store.chikendev._2tm.dto.responce.StoreResponse;
import store.chikendev._2tm.entity.CartItems;
import store.chikendev._2tm.entity.Product;
import store.chikendev._2tm.entity.Store;
import store.chikendev._2tm.exception.AppException;
import store.chikendev._2tm.exception.ErrorCode;
import store.chikendev._2tm.entity.Account;
import store.chikendev._2tm.repository.CartItemsRepository;
import store.chikendev._2tm.repository.ProductRepository;
import store.chikendev._2tm.utils.EntityFileType;
import store.chikendev._2tm.utils.FilesHelp;
import store.chikendev._2tm.repository.AccountRepository;

@Service
public class CartService {

    @Autowired
    private CartItemsRepository cartItemsRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private AccountRepository accountRepository;

    @SuppressWarnings("unused")
    // thêm sản phẩm vào giỏ hàng
    public void addProductToCart(Long idProduct, Integer quantityRequest) {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Account account = accountRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        Product product = productRepository.findById(idProduct)
                .orElseThrow(() -> new RuntimeException("Sản phẩm không tồn tại"));

        CartItems cartItemFound = cartItemsRepository.findCartItemsByAccountIdAndProductId(account.getId(), idProduct);

        if (cartItemFound == null) {
            CartItems cartItem = new CartItems();
            if (product.getQuantity() >= quantityRequest) {
                cartItem.setQuantity(quantityRequest);
            } else {
                throw new AppException(ErrorCode.QUANTITY_ERROR);
            }
            cartItem.setProduct(product);
            cartItem.setAccount(account);
            cartItemsRepository.save(cartItem);
            return;
        } else {
            int quantity = cartItemFound.getQuantity() + quantityRequest;
            if (product.getQuantity() >= quantity) {
                cartItemFound.setQuantity(quantity);
            } else {
                cartItemFound.setQuantity(product.getQuantity());
                throw new AppException(ErrorCode.QUANTITY_ERROR);
            }
            cartItemsRepository.save(cartItemFound);
            return;
        }
        // chỉ cần tìm sản phẩm trong giỏ hàng, nếu có thì cập nhật số lượng, không thì
        // thêm mới
        // không cần lấy tất cả sản phẩm trong giỏ hàng để kiểm tra và cập nhật

        // List<CartItems> addQuantity =
        // cartItemsRepository.getItemsByAccount(account.getId());
        // for (CartItems cart : addQuantity) {
        // if (cart.getProduct().getId() == idProduct) {
        // int quantity = cart.getQuantity() + 1;
        // if (product.getQuantity() >= quantity) {
        // cart.setQuantity(quantity);
        // } else {
        // throw new AppException(ErrorCode.QUANTITY_ERROR);
        // }
        // cartItemsRepository.save(cart);
        // return;
        // }
        // }
        // CartItems cartItem = new CartItems();
        // cartItem.setProduct(product);
        // cartItem.setAccount(account);
        // cartItemsRepository.save(cartItem);
    }

    // Lấy tìm userid qua token
    public List<CartResponse> getUserCart() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Account account = accountRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        List<CartItems> carts = cartItemsRepository.getItemsByAccount(account.getId());
        return carts.stream().map(this::convertToCartDto).collect(Collectors.toList());
    }

    // Lấy thông tin giỏ hàng
    private CartResponse convertToCartDto(CartItems cart) {
        return CartResponse.builder()
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
    private ProductResponse convertToProductDto(Product product) {
        if (product != null) {
            List<AttributeProductResponse> attrs = new ArrayList<>();
            if (product.getAttributes().size() > 0) {
                product.getAttributes().forEach(att -> {
                    attrs.add(AttributeProductResponse.builder()
                            .id(att.getAttributeDetail().getId())
                            .name(att.getAttributeDetail().getAttribute().getName())
                            .value(att.getAttributeDetail().getDescription())
                            .build());
                });
            }
            ResponseDocumentDto image = FilesHelp.getOneDocument(product.getId(), EntityFileType.PRODUCT);
            System.out.println(product.getId());
            ResponseDocumentDto imageStore = FilesHelp.getOneDocument(product.getStore().getId(),
                    EntityFileType.STORE_LOGO);
            return ProductResponse.builder()
                    .id(product.getId())
                    .name(product.getName())
                    .price(product.getPrice())
                    .quantity(product.getQuantity())
                    .description(product.getDescription())
                    .store(StoreResponse.builder()
                            .id(product.getStore().getId())
                            .name(product.getStore().getName())
                            .streetAddress(getStoreAddress(product.getStore()))
                            .urlImage(imageStore.getFileDownloadUri())
                            .build())
                    .attributes(attrs)
                    .thumbnail(image)
                    .build();
        }
        return null;
    }

    // Xóa sản phẩm khỏi giỏ hàng
    public void removeProductFromCart(Long productId) {
        CartItems cartItem = cartItemsRepository.findByProductId(productId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm trong giỏ hàng"));
        cartItemsRepository.delete(cartItem);
    }

}
