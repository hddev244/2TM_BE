package store.chikendev._2tm.service;

import java.util.Optional;
import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import store.chikendev._2tm.dto.request.CreateProductRequest;
import store.chikendev._2tm.dto.responce.AttributeProductResponse;
import store.chikendev._2tm.dto.responce.ProductResponse;
import store.chikendev._2tm.dto.responce.ResponseDocumentDto;
import store.chikendev._2tm.dto.responce.StoreResponse;
import store.chikendev._2tm.entity.Account;
import store.chikendev._2tm.entity.AccountStore;
import store.chikendev._2tm.entity.Category;
import store.chikendev._2tm.entity.Product;
import store.chikendev._2tm.entity.ProductAttributeDetail;
import store.chikendev._2tm.entity.StateProduct;
import store.chikendev._2tm.entity.Store;
import store.chikendev._2tm.exception.AppException;
import store.chikendev._2tm.exception.ErrorCode;
import store.chikendev._2tm.repository.AccountRepository;
import store.chikendev._2tm.repository.AccountStoreRepository;
import store.chikendev._2tm.repository.AttributeDetailRepository;
import store.chikendev._2tm.repository.CategoryRepository;
import store.chikendev._2tm.repository.ProductAttributeDetailRepository;
import store.chikendev._2tm.repository.ProductRepository;
import store.chikendev._2tm.repository.StateProductRepository;
import store.chikendev._2tm.utils.EntityFileType;
import store.chikendev._2tm.utils.FilesHelp;

@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private ModelMapper mapper;

    @Autowired
    private AccountStoreRepository accountStoreRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private StateProductRepository stateProductRepository;

    @Autowired
    private ProductAttributeDetailRepository productAttributeDetailRepository;

    @Autowired
    private AttributeDetailRepository attributeDetailRepository;

    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

    public ProductResponse staffCreateProduct(CreateProductRequest request, MultipartFile[] files) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Account account = accountRepository.findByEmail(email).orElseThrow(() -> {
            throw new AppException(ErrorCode.USER_NOT_FOUND);
        });
        Category category = categoryRepository.findById(request.getIdCategory()).orElseThrow(() -> {
            throw new AppException(ErrorCode.CATEGORY_NOT_FOUND);
        });
        StateProduct state = stateProductRepository.findById(StateProduct.CONFIRM).orElseThrow(() -> {
            throw new AppException(ErrorCode.STATE_NOT_FOUND);
        });
        Optional<AccountStore> store = accountStoreRepository.findByAccount(account);

        Product product = mapper.map(request, Product.class);
        product.setAccount(account);
        product.setType(Product.TYPE_PRODUCT_OF_STORE);
        product.setCategory(category);
        product.setState(state);
        if (store.isPresent()) {
            product.setStore(store.get().getStore());
        } else {
            throw new AppException(ErrorCode.STORE_NOT_FOUND);
        }
        // lưu ảnh
        Product save = productRepository.save(product);
        for (MultipartFile file : files) {
            FilesHelp.saveFile(file, save.getId(), EntityFileType.PRODUCT);
        }
        // lưu attribute
        List<ProductAttributeDetail> attributeDetails = new ArrayList<>();
        request.getIdAttributeDetail().forEach(id -> {
            ProductAttributeDetail attributeDetail = ProductAttributeDetail.builder()
                    .product(save)
                    .attributeDetail(attributeDetailRepository.findById(id).orElseThrow(() -> {
                        throw new AppException(ErrorCode.ATTRIBUTE_NOT_FOUND);
                    }))
                    .build();
            attributeDetails.add(attributeDetail);
        });
        List<ProductAttributeDetail> saveAttribute = productAttributeDetailRepository.saveAll(attributeDetails);
        List<AttributeProductResponse> attrs = new ArrayList<>();
        saveAttribute.forEach(att -> {
            attrs.add(AttributeProductResponse.builder()
                    .id(att.getAttributeDetail().getAttribute().getId())
                    .name(att.getAttributeDetail().getAttribute().getName())
                    .value(att.getAttributeDetail().getDescription())
                    .build());
        });
        // store response
        StoreResponse responseStore = mapper.map(save.getStore(), StoreResponse.class);
        ResponseDocumentDto imageStore = FilesHelp.getOneDocument(save.getStore().getId(),
                EntityFileType.STORE_LOGO);
        responseStore.setUrlImage(imageStore.getFileDownloadUri());
        responseStore.setStreetAddress(getStoreAddress(save.getStore()));

        // product response
        ProductResponse response = convertToResponse(save);
        response.setIdCategory(product.getCategory().getId());
        response.setStore(responseStore);
        response.setAttributes(attrs);

        return response;

    }

    public Page<ProductResponse> getAllProducts(Pageable pageable) {
        Page<Product> products = productRepository.findAvailableProducts(pageable);
        Page<ProductResponse> productResponses = products.map(product -> {
            var thumbnail = FilesHelp.getOneDocument(product.getId(), EntityFileType.PRODUCT);
            var address = getStoreAddress(product.getStore());
            var storeName = product.getStore() == null ? "" : product.getStore().getName();
            var type = "";
            if (product.getType() != null) {
                type = product.getType() ? "Cửa hàng" : "Ký gửi";
            }
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

            return ProductResponse.builder()
                    .id(product.getId())
                    .thumbnail(thumbnail)
                    .name(product.getName())
                    .price(product.getPrice())
                    .quantity(product.getQuantity())
                    .typeProduct(type)
                    .attributes(attrs)
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

    public Page<ProductResponse> getAvailableProductsByCategory(Long categoryId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Product> products = productRepository.findAvailableProductsByCategory(categoryId, pageable);

        Page<ProductResponse> productResponses = products.map(product -> {
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

            StoreResponse store = null;
            if (product.getStore() != null) {
                store = mapper.map(product.getStore(), StoreResponse.class);
                ResponseDocumentDto imageStore = FilesHelp.getOneDocument(store.getId(), EntityFileType.STORE_LOGO);
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

}