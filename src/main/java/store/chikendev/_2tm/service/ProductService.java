package store.chikendev._2tm.service;

import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

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

    public ProductResponse getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not find ProductById: " + id));
        return mapper.map(product, ProductResponse.class);
    }

    public Page<ProductResponse> getAllProducts(Pageable pageable) {
        Page<Product> products = productRepository.findAll(pageable);
        Page<ProductResponse> productResponses = products.map(product -> {
            var thumbnail = FilesHelp.getOneDocument(product.getId(), EntityFileType.PRODUCT);
            var address = "";
            if (product.getStore().getWard() != null && product.getStore().getWard().getDistrict() != null
                    && product.getStore().getWard().getDistrict().getProvinceCity() != null){
                        String StoreWard = product.getStore().getWard().getName();
                        String StoreDistrict = product.getStore().getWard().getDistrict().getName();
                        String StoreProvince = product.getStore().getWard().getDistrict().getProvinceCity().getName();
                        String storeAddress = product.getStore().getStreetAddress();
                        address = storeAddress + ", " + StoreWard + ", " + StoreDistrict + ", " + StoreProvince;
            }

            return ProductResponse.builder()
                    .id(product.getId())
                    .thumbnail(thumbnail)
                    .name(product.getName())
                    .price(product.getPrice())
                    .quantity(product.getQuantity())
                    .store(
                            StoreResponse.builder()
                                    .name(product.getStore().getName())
                                    .streetAddress(address)
                                    .build()
                    )
                    .build();
        });
        return productResponses;
    }

    private ProductResponse convertToResponse(Product product) {
        ProductResponse response = new ProductResponse();
        response.setId(product.getId());
        response.setName(product.getName());
        response.setPrice(product.getPrice());
        response.setQuantity(product.getQuantity());
        response.setDescription(product.getDescription());
        ResponseDocumentDto responseDocument = FilesHelp.getOneDocument(response.getId(), EntityFileType.CATEGORY);
        response.setThumbnail(responseDocument);
        return response;
    }
}