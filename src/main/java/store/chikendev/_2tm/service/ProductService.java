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

import jakarta.validation.Valid;
import store.chikendev._2tm.dto.request.ConsignmentOrdersRequest;
import store.chikendev._2tm.dto.request.CreateProductRequest;
import store.chikendev._2tm.dto.request.ProductRequest;
import store.chikendev._2tm.dto.responce.AttributeProductResponse;
import store.chikendev._2tm.dto.responce.ConsignmentOrdersResponse;
import store.chikendev._2tm.dto.responce.ProductResponse;
import store.chikendev._2tm.dto.responce.ResponseDocumentDto;
import store.chikendev._2tm.dto.responce.StoreResponse;
import store.chikendev._2tm.entity.Account;
import store.chikendev._2tm.entity.AccountStore;
import store.chikendev._2tm.entity.Category;
import store.chikendev._2tm.entity.ConsignmentOrders;
import store.chikendev._2tm.entity.Image;
import store.chikendev._2tm.entity.Product;
import store.chikendev._2tm.entity.ProductAttributeDetail;
import store.chikendev._2tm.entity.ProductImages;
import store.chikendev._2tm.entity.Role;
import store.chikendev._2tm.entity.StateConsignmentOrder;
import store.chikendev._2tm.entity.StateProduct;
import store.chikendev._2tm.entity.Store;
import store.chikendev._2tm.entity.Ward;
import store.chikendev._2tm.exception.AppException;
import store.chikendev._2tm.exception.ErrorCode;
import store.chikendev._2tm.repository.AccountRepository;
import store.chikendev._2tm.repository.AccountStoreRepository;
import store.chikendev._2tm.repository.AttributeDetailRepository;
import store.chikendev._2tm.repository.CategoryRepository;
import store.chikendev._2tm.repository.ConsignmentOrdersRepository;
import store.chikendev._2tm.repository.ImageRepository;
import store.chikendev._2tm.repository.ProductAttributeDetailRepository;
import store.chikendev._2tm.repository.ProductImagesRepository;
import store.chikendev._2tm.repository.ProductRepository;
import store.chikendev._2tm.repository.RoleRepository;
import store.chikendev._2tm.repository.StateConsignmentOrderRepository;
import store.chikendev._2tm.repository.StateProductRepository;
import store.chikendev._2tm.repository.StoreRepository;
import store.chikendev._2tm.repository.WardRepository;
import store.chikendev._2tm.utils.EntityFileType;
import store.chikendev._2tm.utils.FilesHelp;

@Service
public class ProductService {

    @Autowired
    private ConsignmentOrdersRepository consignmentOrdersRepository;

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    ProductImagesRepository productImagesRepository;

    @Autowired
    private StateConsignmentOrderRepository stateConsignmentOrderRepository;

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private WardRepository wardRepository;
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
    private ConsignmentOrdersService consignmentOrdersService;

    @Autowired
    private StateProductRepository stateProductRepository;

    @Autowired
    private ProductAttributeDetailRepository productAttributeDetailRepository;

    @Autowired
    private AttributeDetailRepository attributeDetailRepository;

    @Autowired
    private RoleRepository roleRepository;

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
        List<ProductImages> images = saveProductImages(save, files);
        save.setImages(images);
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
            ResponseDocumentDto thumbnail = null;
            // thay đổi ảnh thumbnail bằng image từ db
            if (product.getImages().size() > 0) {
                var image = product.getImages().get(0).getImage();
                thumbnail = ResponseDocumentDto.builder()
                        .fileId(image.getFileId())
                        .fileName(image.getFileName())
                        .fileDownloadUri(image.getFileDownloadUri())
                        .fileType(image.getFileType())
                        .size(image.getSize())
                        .build();
            }

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

        List<ResponseDocumentDto> responseDocument = product.getImages().stream().map(img -> {
            Image image = img.getImage();
            return ResponseDocumentDto.builder()
                    .fileId(image.getFileId())
                    .fileName(image.getFileName())
                    .fileDownloadUri(image.getFileDownloadUri())
                    .fileType(image.getFileType())
                    .size(image.getSize())
                    .build();
        }).toList();
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

    public ConsignmentOrdersResponse ownerCreateProduct(@Valid ConsignmentOrdersRequest request,
            MultipartFile[] images) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Account account = accountRepository.findByEmail(email).orElseThrow(() -> {
            throw new AppException(ErrorCode.USER_NOT_FOUND);
        });
        Category category = categoryRepository.findById(request.getIdCategory()).orElseThrow(() -> {
            throw new AppException(ErrorCode.CATEGORY_NOT_FOUND);
        });
        StateProduct stateProduct = stateProductRepository.findById(StateProduct.IN_CONFIRM).orElseThrow(() -> {
            throw new AppException(ErrorCode.STATE_NOT_FOUND);
        });
        Store store = storeRepository.findById(request.getStoreId()).orElseThrow(() -> {
            throw new AppException(ErrorCode.STORE_NOT_FOUND);
        });

        Ward ward = wardRepository.findById(request.getWardId()).orElseThrow(() -> {
            throw new AppException(ErrorCode.WARD_NOT_FOUND);
        });

        Optional<Account> deliveryPerson = store.getAccountStores().stream()
                .flatMap(acc -> acc.getAccount().getRoles().stream()
                        .filter(role -> role.getRole().getId().equals("NVGH"))
                        .map(role -> acc.getAccount()))
                .findFirst();

        if (deliveryPerson.isPresent() == false) {
            throw new AppException(ErrorCode.DELIVERY_PERSON_NOT_FOUND);
        }

        Product product = Product.builder()
                .name(request.getName())
                .price(request.getPrice())
                .quantity(request.getQuantity())
                .description(request.getDescription())
                .category(category)
                .state(stateProduct)
                .store(store)
                .ownerId(account)
                .build();

        // lưu ảnh
        Product saveProduct = productRepository.save(product);

        List<ProductImages> imagesSave = saveProductImages(saveProduct, images);
        saveProduct.setImages(imagesSave);
        // lưu attribute
        List<ProductAttributeDetail> attributeDetails = new ArrayList<>();
        request.getIdAttributeDetail().forEach(id -> {
            ProductAttributeDetail attributeDetail = ProductAttributeDetail.builder()
                    .product(saveProduct)
                    .attributeDetail(attributeDetailRepository.findById(id).orElseThrow(() -> {
                        throw new AppException(ErrorCode.ATTRIBUTE_NOT_FOUND);
                    }))
                    .build();
            attributeDetails.add(attributeDetail);
        });
        productAttributeDetailRepository.saveAll(attributeDetails);

        StateConsignmentOrder state = stateConsignmentOrderRepository.findById(StateConsignmentOrder.IN_CONFIRM).get();

        ConsignmentOrders save = ConsignmentOrders.builder()
                .note(request.getNote())
                .ordererId(account)
                .product(saveProduct)
                .store(store)
                .stateId(state)
                .phoneNumber(request.getPhoneNumber())
                .detailAddress(request.getDetailAddress())
                .ward(ward)
                .deliveryPerson(deliveryPerson.get())
                .build();

        consignmentOrdersRepository.save(save);

        ConsignmentOrdersResponse response = consignmentOrdersService.convertToConsignmentOrdersResponse(save);

        return response;
    }

    private List<ProductImages> saveProductImages(Product product, MultipartFile[] images) {
        List<ProductImages> productImages = new ArrayList<>();
        for (MultipartFile file : images) {
            ResponseDocumentDto fileSaved = FilesHelp.saveFile(file, product.getId(), EntityFileType.PRODUCT);
            Image image = Image.builder()
                    .fileId(fileSaved.getFileId())
                    .fileName(fileSaved.getFileName())
                    .fileDownloadUri(fileSaved.getFileDownloadUri())
                    .fileType(fileSaved.getFileType())
                    .size(fileSaved.getSize())
                    .build();
            Image imageSaved = imageRepository.save(image);

            ProductImages productImage = ProductImages.builder()
                    .product(product)
                    .image(imageSaved)
                    .build();
            productImages.add(productImage);
        }
        System.out.println(productImages.get(0).getImage().getFileDownloadUri());
        return productImagesRepository.saveAll(productImages);
    }

        public ProductResponse updateProduct(Long id, ProductRequest productRequest) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Account account = accountRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        Role role = roleRepository.findByAccountId(account.getId())
                .orElseThrow(() -> new AppException(ErrorCode.LOGIN_ROLE_REQUIRED));
        if (!role.getName().equals(Role.ROLE_STORE_MANAGER) && !role.getName().equals(Role.ROLE_PRODUCT_OWNER)) {
            throw new AppException(ErrorCode.LOGIN_ROLE_REQUIRED);
        }

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

        Store store = product.getStore();
        if (store == null || !isManagerOfStore(account, store)) {
            throw new AppException(ErrorCode.NO_MANAGEMENT_RIGHTS);
        }

        product.setName(productRequest.getName());
        product.setPrice(productRequest.getPrice());
        product.setQuantity(productRequest.getQuantity());
        product.setDescription(productRequest.getDescription());

        if (productRequest.getCategoryId() != null) {
            Category category = categoryRepository.findById(productRequest.getCategoryId())
                    .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));
            product.setCategory(category);
        }

        product = productRepository.save(product);
        return convertToResponse(product);
    }

    private boolean isManagerOfStore(Account account, Store store) {
        return store.getAccountStores().stream()
                .anyMatch(accountStore -> accountStore.getAccount().equals(account));
    }
}