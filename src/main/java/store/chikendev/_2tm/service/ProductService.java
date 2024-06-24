package store.chikendev._2tm.service;

import java.util.Optional;
import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import store.chikendev._2tm.dto.request.RequestProduct;
import store.chikendev._2tm.dto.responce.AttributeProductResponse;
import store.chikendev._2tm.dto.responce.ProductResponse;
import store.chikendev._2tm.dto.responce.ResponseDocumentDto;
import store.chikendev._2tm.dto.responce.StoreResponse;
import store.chikendev._2tm.entity.Account;
import store.chikendev._2tm.entity.AccountStore;
import store.chikendev._2tm.entity.Product;
import store.chikendev._2tm.entity.Store;
import store.chikendev._2tm.exception.AppException;
import store.chikendev._2tm.exception.ErrorCode;
import store.chikendev._2tm.repository.AccountRepository;
import store.chikendev._2tm.repository.AccountStoreRepository;
import store.chikendev._2tm.repository.ProductRepository;
import store.chikendev._2tm.repository.StoreRepository;
import store.chikendev._2tm.utils.EntityFileType;
import store.chikendev._2tm.utils.FilesHelp;

@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private ModelMapper mapper;

    @Autowired
    private AccountStoreRepository accountStoreRepository;

    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

    public ProductResponse createProduct(RequestProduct request) {
        Optional<Account> accountOpt = accountRepository.findById(request.getAccountId());
        Optional<Store> storeOpt = storeRepository.findById(request.getStoreId());

        if (accountOpt.isPresent() || storeOpt.isPresent()) {
            Account account = accountOpt.get();
            Store store = storeOpt.get();

            // Kiểm tra liên kết giữa tài khoản và cửa hàng
            Optional<AccountStore> accountStoreOpt = accountStoreRepository.findByAccountAndStore(account, store);
            if (accountStoreOpt.isPresent()) {
                Product product = new Product();
                product.setName(request.getName());
                product.setPrice(request.getPrice());
                product.setQuantity(request.getQuantity());
                product.setDescription(request.getDescription());
                product.setAccount(account);
                product.setStore(store);
                Product savedProduct = productRepository.save(product);
                return convertToResponse(savedProduct);
            } else {
                throw new IllegalArgumentException("Account is not linked with the store");
            }
        } else {
            throw new IllegalArgumentException("Invalid accountId or storeId");
        }
    }

    public ProductResponse updateProduct(Long id, RequestProduct request) {
        Optional<Account> accountOpt = accountRepository.findById(request.getAccountId());
        Optional<Store> storeOpt = storeRepository.findById(request.getStoreId());

        if (accountOpt.isPresent() || storeOpt.isPresent()) {
            Account account = accountOpt.get();
            Store store = storeOpt.get();

            // Kiểm tra liên kết giữa tài khoản và cửa hàng
            Optional<AccountStore> accountStoreOpt = accountStoreRepository.findByAccountAndStore(account, store);
            if (accountStoreOpt.isPresent()) {
                Optional<Product> existingProductOpt = productRepository.findById(id);
                if (existingProductOpt.isPresent()) {
                    Product existingProduct = existingProductOpt.get();
                    existingProduct.setName(request.getName());
                    existingProduct.setPrice(request.getPrice());
                    existingProduct.setQuantity(request.getQuantity());
                    existingProduct.setDescription(request.getDescription());
                    existingProduct.setAccount(account);
                    existingProduct.setStore(store);

                    Product savedProduct = productRepository.save(existingProduct);
                    return convertToResponse(savedProduct);
                } else {
                    throw new IllegalArgumentException("Product not found");
                }
            } else {
                throw new IllegalArgumentException("Account is not linked with the store");
            }
        } else {
            throw new IllegalArgumentException("Invalid accountId or storeId");
        }
    }

    public Page<ProductResponse> getAllProducts(Pageable pageable) {
        Page<Product> products = productRepository.findAll(pageable);
        Page<ProductResponse> productResponses = products.map(product -> {
            var thumbnail = FilesHelp.getOneDocument(product.getId(), EntityFileType.PRODUCT);
            var address = getStoreAddress(product.getStore());
            var storeName = product.getStore() == null ? "" : product.getStore().getName();
            var type = "";
            if (product.getType() != null) {
                type = product.getType() ? "Cửa hàng" : "Ký gửi";
            }

            return ProductResponse.builder()
                    .id(product.getId())
                    .thumbnail(thumbnail)
                    .name(product.getName())
                    .price(product.getPrice())
                    .quantity(product.getQuantity())
                    .typeProduct(type)
                    .store(
                            StoreResponse.builder()
                                    .name(storeName)
                                    .streetAddress(address)
                                    .build())
                    .build();
        });
        return productResponses;
    }

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

    private ProductResponse convertToResponse(Product product) {
        ProductResponse response = new ProductResponse();
        response.setId(product.getId());
        response.setName(product.getName());
        response.setPrice(product.getPrice());
        response.setQuantity(product.getQuantity());
        response.setDescription(product.getDescription());
        if (product.getType() != null) {
            response.setTypeProduct(product.getType() ? "Cửa hàng" : "Ký gửi");
        }

        List<ResponseDocumentDto> responseDocument = FilesHelp.getDocuments(response.getId(), EntityFileType.PRODUCT);
        response.setImages(responseDocument);
        return response;
    }

    public ProductResponse getById(Long id) {
        Product product = productRepository.findById(id).orElseThrow(() -> {
            throw new AppException(ErrorCode.PRODUCT_NOT_FOUND);
        });

        List<AttributeProductResponse> attrs = new ArrayList<>();

        if (product.getAttributes().size() > 0) {
            product.getAttributes().forEach(att -> {
                attrs.add(AttributeProductResponse.builder()
                        .id(att.getAttributeDetail().getAttribute().getId())
                        .name(att.getAttributeDetail().getAttribute().getName())
                        .value(att.getAttributeDetail().getDescription())
                        .build());
            });
        }
        StoreResponse store = mapper.map(product.getStore(), StoreResponse.class);
        ResponseDocumentDto imageStore = FilesHelp.getOneDocument(store.getId(), EntityFileType.STORE_LOGO);
        store.setUrlImage(imageStore.getFileDownloadUri());
        store.setStreetAddress(getStoreAddress(product.getStore()));

        ProductResponse response = convertToResponse(product);
        response.setAttributes(attrs);
        response.setStore(store);
        response.setIdCategory(product.getCategory().getId());

        return response;

    }

    public Page<ProductResponse> getByNameAndDescription(String value) {
        Pageable pageable = PageRequest.of(0, 20);
        Page<Product> products = productRepository.findProductsBySearchTerm(value, pageable);
        System.out.println(products.getContent().size());
        Page<ProductResponse> productResponses = products.map(product -> {
            List<AttributeProductResponse> attrs = new ArrayList<>();
            if (product.getAttributes().size() > 0) {
                product.getAttributes().forEach(att -> {
                    attrs.add(AttributeProductResponse.builder()
                            .id(att.getAttributeDetail().getAttribute().getId())
                            .name(att.getAttributeDetail().getAttribute().getName())
                            .value(att.getAttributeDetail().getDescription())
                            .build());
                });
            }
            StoreResponse store = null;
            if (product.getStore() != null) {
                store = mapper.map(product.getStore(), StoreResponse.class);
                ResponseDocumentDto imageStore = FilesHelp.getOneDocument(store.getId(),
                        EntityFileType.STORE_LOGO);
                store.setUrlImage(imageStore.getFileDownloadUri());
                store.setStreetAddress(getStoreAddress(product.getStore()));
            }
            ProductResponse response = convertToResponse(product);
            response.setAttributes(attrs);
            response.setStore(store);
            response.setIdCategory(product.getCategory().getId());

            return response;
        });
        return productResponses;

    }

    // fix làm giống getByNameAndDescription thay đổi
    // productRepository.findByQuantityGreaterThanOrderByCreatedAtDesc(0); vô còn
    // lại y trang

    // public List<ProductResponse> getProducts() {
    // List<Product> products =
    // productRepository.findByQuantityGreaterThanOrderByCreatedAtDesc(0);
    // return
    // products.stream().map(this::mapToResponse).collect(Collectors.toList());
    // }

}