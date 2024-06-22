package store.chikendev._2tm.service;

import java.util.Optional;
import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import store.chikendev._2tm.dto.responce.AttributeProductResponse;
import store.chikendev._2tm.dto.responce.ProductResponse;
import store.chikendev._2tm.dto.responce.ResponseDocumentDto;
import store.chikendev._2tm.dto.responce.StoreResponse;
import store.chikendev._2tm.entity.Account;
import store.chikendev._2tm.entity.Product;
import store.chikendev._2tm.entity.Store;
import store.chikendev._2tm.exception.AppException;
import store.chikendev._2tm.exception.ErrorCode;
import store.chikendev._2tm.repository.AccountRepository;
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

    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

    public Product createProduct(String name, Double price, Integer quantity, String description, String accountId,
            Long storeId) {
        Optional<Account> accountOpt = accountRepository.findById(accountId);
        Optional<Store> storeOpt = storeRepository.findById(storeId);

        if (accountOpt.isPresent() || storeOpt.isPresent()) {
            Product product = new Product();
            product.setName(name);
            product.setPrice(price);
            product.setQuantity(quantity);
            product.setDescription(description);
            product.setAccount(accountOpt.get());
            product.setStore(storeOpt.get());

            return productRepository.save(product);
        } else {
            throw new IllegalArgumentException("Invalid accountId or storeId");
        }
    }

    public Product updateProduct(Long id, String name, Double price, Integer quantity, String description,
            String accountId, Long storeId) {
        Optional<Account> accountOpt = accountRepository.findById(accountId);
        Optional<Store> storeOpt = storeRepository.findById(storeId);

        if (accountOpt.isPresent() && storeOpt.isPresent()) {
            Optional<Product> existingProductOpt = productRepository.findById(id);
            if (existingProductOpt.isPresent()) {
                Product existingProduct = existingProductOpt.get();
                existingProduct.setName(name);
                existingProduct.setPrice(price);
                existingProduct.setQuantity(quantity);
                existingProduct.setDescription(description);
                existingProduct.setAccount(accountOpt.get());
                existingProduct.setStore(storeOpt.get());
                return productRepository.save(existingProduct);
            } else {
                throw new AppException(ErrorCode.USER_EXISTED);// fix
            }
        } else {
            throw new IllegalArgumentException("Invalid accountId or storeId");
        }
    }

    public Page<ProductResponse> getAllProducts(Pageable pageable) {
        Page<Product> products = productRepository.findAll(pageable);
        Page<ProductResponse> productResponses = products.map(product -> {
            var thumbnail = FilesHelp.getDocuments(product.getId(), EntityFileType.PRODUCT);
            var address = getStoreAddress(product.getStore());
            var storeName = product.getStore() == null ? "" : product.getStore().getName();

            return ProductResponse.builder()
                    .id(product.getId())
                    .thumbnail(thumbnail.stream().map(image -> {
                        return image.getFileDownloadUri();
                    }).toList())
                    .name(product.getName())
                    .price(product.getPrice())
                    .quantity(product.getQuantity())
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
        List<ResponseDocumentDto> responseDocument = FilesHelp.getDocuments(response.getId(), EntityFileType.CATEGORY);
        response.setThumbnail(responseDocument.stream().map(image -> {
            return image.getFileDownloadUri();
        }).toList());
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
                        .id(att.getId())
                        .name(att.getAttributeDetail().getAttribute().getName())
                        .value(att.getAttributeDetail().getDescription())
                        .build());
            });
        }
        StoreResponse store = mapper.map(product.getStore(), StoreResponse.class);
        ResponseDocumentDto imageStore = FilesHelp.getOneDocument(store.getId(), EntityFileType.STORE_LOGO);
        store.setUrlImage(imageStore.getFileDownloadUri());
        store.setStreetAddress(store.getStreetAddress() + ", " + product.getStore().getWard().getName() + ", "
                + product.getStore().getWard().getDistrict().getName() + ", "
                + product.getStore().getWard().getDistrict().getProvinceCity().getName());

        ProductResponse response = convertToResponse(product);
        response.setAttributes(attrs);
        response.setStore(store);
        response.setIdCategory(product.getCategory().getId());

        return response;

    }
}