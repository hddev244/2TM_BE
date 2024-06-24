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

    public void addProductToCart(Long idProduct) {
        Product product = productRepository.findById(idProduct)
                .orElseThrow(() -> new RuntimeException("Sản phẩm không tồn tại"));
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Account account = accountRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        List<CartItems> addQuantity = cartItemsRepository.getItemsByAccount(account.getId());
        for (CartItems cart : addQuantity) {
            if (cart.getProduct().getId() == idProduct) {
                int quantity = cart.getQuantity() + 1;
                if (product.getQuantity() >= quantity) {
                    cart.setQuantity(quantity);
                } else {
                    throw new AppException(ErrorCode.QUANTITY_ERROR);
                }
                cartItemsRepository.save(cart);
                return;
            }
        }
        CartItems cartItem = new CartItems();
        cartItem.setProduct(product);
        cartItem.setAccount(account);
        cartItemsRepository.save(cartItem);
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
    private List<ProductResponse> convertToProductDto(Product product) {
        List<ProductResponse> productResponses = new ArrayList<>();
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
            List<ResponseDocumentDto> images = FilesHelp.getDocuments(product.getId(), EntityFileType.PRODUCT);
            System.out.println(product.getId());
            ResponseDocumentDto imageStore = FilesHelp.getOneDocument(product.getStore().getId(),
                    EntityFileType.STORE_LOGO);
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
                            .urlImage(imageStore.getFileDownloadUri())
                            .build())
                    .attributes(attrs)
                    .images(images)
                    .build());
        }

        return productResponses;
    }

}
