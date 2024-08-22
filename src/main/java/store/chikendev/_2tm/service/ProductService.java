package store.chikendev._2tm.service;

import jakarta.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import store.chikendev._2tm.dto.request.ConsignmentOrdersRequest;
import store.chikendev._2tm.dto.request.CreateProductRequest;
import store.chikendev._2tm.dto.request.NotificationPayload;
import store.chikendev._2tm.dto.request.ProductRequest;
import store.chikendev._2tm.dto.responce.AttributeProductResponse;
import store.chikendev._2tm.dto.responce.CategoryResponse;
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
import store.chikendev._2tm.entity.RoleAccount;
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
import store.chikendev._2tm.repository.RoleAccountRepository;
import store.chikendev._2tm.repository.StateConsignmentOrderRepository;
import store.chikendev._2tm.repository.StateProductRepository;
import store.chikendev._2tm.repository.StoreRepository;
import store.chikendev._2tm.repository.WardRepository;
import store.chikendev._2tm.utils.EntityFileType;
import store.chikendev._2tm.utils.FilesHelp;
import store.chikendev._2tm.utils.dtoUtil.response.ImageDtoUtil;
import store.chikendev._2tm.utils.service.AccountServiceUtill;

@Service
public class ProductService {

        @Autowired
        private AccountServiceUtill accountServiceUtill;

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
        NotificationService notificationService;

        @Autowired
        private WardRepository wardRepository;

        @Autowired
        private ProductRepository productRepository;

        @Autowired
        private AccountRepository accountRepository;

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
        private RoleAccountRepository roleAccountRepository;

        public void deleteProduct(Long id) {
                productRepository.deleteById(id);
        }

        public ProductResponse staffCreateProduct(
                        CreateProductRequest request,
                        MultipartFile[] files) {
                String email = SecurityContextHolder.getContext()
                                .getAuthentication()
                                .getName();
                Account account = accountRepository
                                .findByEmail(email)
                                .orElseThrow(() -> {
                                        throw new AppException(ErrorCode.USER_NOT_FOUND);
                                });
                Category category = categoryRepository
                                .findById(request.getIdCategory())
                                .orElseThrow(() -> {
                                        throw new AppException(ErrorCode.CATEGORY_NOT_FOUND);
                                });
                StateProduct state = stateProductRepository
                                .findById(StateProduct.CONFIRM)
                                .orElseThrow(() -> {
                                        throw new AppException(ErrorCode.STATE_NOT_FOUND);
                                });
                Optional<AccountStore> store = accountStoreRepository.findByAccount(
                                account);

                Product product = new Product();
                product.setName(request.getName());
                product.setPrice(request.getPrice());
                product.setQuantity(request.getQuantity());
                product.setDescription(request.getDescription());
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
                request
                                .getIdAttributeDetail()
                                .forEach(id -> {
                                        ProductAttributeDetail attributeDetail = ProductAttributeDetail.builder()
                                                        .product(save)
                                                        .attributeDetail(
                                                                        attributeDetailRepository
                                                                                        .findById(id)
                                                                                        .orElseThrow(() -> {
                                                                                                throw new AppException(
                                                                                                                ErrorCode.ATTRIBUTE_NOT_FOUND);
                                                                                        }))
                                                        .build();
                                        attributeDetails.add(attributeDetail);
                                });
                List<ProductAttributeDetail> saveAttribute = productAttributeDetailRepository.saveAll(attributeDetails);
                save.setAttributes(saveAttribute);
                // product response
                ProductResponse response = convertToResponse(save, true);
                return response;
        }

        // khach hang
        // lấy sản phẩm đang bán để show lên trang chủ -
        // ** nếu laấy để làm chức năng khác thì tạo cái riêng
        // không thêm chức nang khác vào đây
        public Page<ProductResponse> getAllProducts(Pageable pageable) {
                Page<Product> products = productRepository.findAvailableProducts(
                                pageable);
                Page<ProductResponse> productResponses = products.map(product -> {
                        ProductResponse response = convertToResponse(product, false);
                        return response;
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
                        String StoreProvince = store
                                        .getWard()
                                        .getDistrict()
                                        .getProvinceCity()
                                        .getName();
                        String storeAddress = store.getStreetAddress() == null
                                        ? ""
                                        : store.getStreetAddress() + ", ";
                        return (storeAddress +
                                        StoreWard +
                                        ", " +
                                        StoreDistrict +
                                        ", " +
                                        StoreProvince);
                }
                return "";
        }

        public ProductResponse convertToResponse(
                        Product product,
                        boolean multiImage) {
                ProductResponse response = new ProductResponse();
                response.setId(product.getId());
                response.setName(product.getName());
                response.setPrice(product.getPrice());
                response.setQuantity(product.getQuantity());
                response.setDescription(product.getDescription());
                List<AttributeProductResponse> attrs = new ArrayList<>();
                if (product.getAttributes().size() > 0) {
                        product
                                        .getAttributes()
                                        .forEach(att -> {
                                                attrs.add(
                                                                AttributeProductResponse.builder()
                                                                                .id(att.getAttributeDetail()
                                                                                                .getAttribute().getId())
                                                                                .name(
                                                                                                att
                                                                                                                .getAttributeDetail()
                                                                                                                .getAttribute()
                                                                                                                .getName())
                                                                                .value(att.getAttributeDetail()
                                                                                                .getDescription())
                                                                                .build());
                                        });
                }
                response.setAttributes(attrs);
                if (product.getCategory() != null) {
                        response.setCategory(
                                        CategoryResponse.builder()
                                                        .id(product.getCategory().getId())
                                                        .name(product.getCategory().getName())
                                                        .path(product.getCategory().getPath())
                                                        .build());
                }
                if (product.getStore() != null) {
                        response.setStore(
                                        StoreResponse.builder()
                                                        .id(product.getStore().getId())
                                                        .name(product.getStore().getName())
                                                        .streetAddress(getStoreAddress(product.getStore()))
                                                        .build());
                }
                response.setState(product.getState());
                if (product.getType() != null) {
                        response.setTypeProduct(product.getType() ? "Cửa hàng" : "Ký gửi");
                }
                List<ResponseDocumentDto> responseDocument = product
                                .getImages()
                                .stream()
                                .map(img -> {
                                        Image image = img.getImage();
                                        return ImageDtoUtil.convertToImageResponse(image);
                                })
                                .toList();
                if (multiImage) {
                        if (responseDocument.size() > 0) {
                                response.setImages(responseDocument);
                        }
                } else {
                        if (responseDocument.size() > 0) {
                                response.setThumbnail(responseDocument.get(0));
                        }
                }
                return response;
        }

        public ProductResponse getById(Long id) {
                Product product = productRepository
                                .findById(id)
                                .orElseThrow(() -> {
                                        throw new AppException(ErrorCode.PRODUCT_NOT_FOUND);
                                });
                return convertToResponse(product, true);
        }

        public Page<ProductResponse> getByNameAndDescription(
                        String value,
                        Integer pageIndex,
                        Integer size) {
                if (pageIndex == null) {
                        pageIndex = 0;
                }
                if (size == null) {
                        size = 8;
                }

                Pageable pageable = PageRequest.of(pageIndex, size);
                Page<Product> products = productRepository.findProductsBySearchTerm(
                                "%" + value + "%",
                                pageable);

                Page<ProductResponse> productResponses = products.map(product -> {
                        return convertToResponse(product, false);
                });

                return productResponses;
        }

        public ConsignmentOrdersResponse ownerCreateProduct(
                        @Valid ConsignmentOrdersRequest request,
                        MultipartFile[] images) {
                Account account = accountServiceUtill.getAccount();
                Category category = categoryRepository
                                .findById(request.getIdCategory())
                                .orElseThrow(() -> {
                                        throw new AppException(ErrorCode.CATEGORY_NOT_FOUND);
                                });
                StateProduct stateProduct = stateProductRepository
                                .findById(StateProduct.DELYVERING)
                                .orElseThrow(() -> {
                                        throw new AppException(ErrorCode.STATE_NOT_FOUND);
                                });
                Store store = storeRepository
                                .findById(request.getStoreId())
                                .orElseThrow(() -> {
                                        throw new AppException(ErrorCode.STORE_NOT_FOUND);
                                });

                Ward ward = wardRepository
                                .findById(request.getWardId())
                                .orElseThrow(() -> {
                                        throw new AppException(ErrorCode.WARD_NOT_FOUND);
                                });

                Optional<Account> deliveryPerson = store
                                .getAccountStores()
                                .stream()
                                .flatMap(acc -> acc
                                                .getAccount()
                                                .getRoles()
                                                .stream()
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
                // lưu attribute
                List<ProductAttributeDetail> attributeDetails = new ArrayList<>();
                request
                                .getIdAttributeDetail()
                                .forEach(id -> {
                                        ProductAttributeDetail attributeDetail = ProductAttributeDetail.builder()
                                                        .product(saveProduct)
                                                        .attributeDetail(
                                                                        attributeDetailRepository
                                                                                        .findById(id)
                                                                                        .orElseThrow(() -> {
                                                                                                throw new AppException(
                                                                                                                ErrorCode.ATTRIBUTE_NOT_FOUND);
                                                                                        }))
                                                        .build();
                                        attributeDetails.add(attributeDetail);
                                });
                productAttributeDetailRepository.saveAll(attributeDetails);
                StateConsignmentOrder state = stateConsignmentOrderRepository
                                .findById(StateConsignmentOrder.CREATED)
                                .get();
                ConsignmentOrders consignmentOrderSaved = ConsignmentOrders.builder()
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
                consignmentOrdersRepository.save(consignmentOrderSaved);
                saveProduct.setImages(imagesSave);
                consignmentOrderSaved.setProduct(saveProduct);
                ConsignmentOrdersResponse response = consignmentOrdersService.convertToConsignmentOrdersResponse(
                                consignmentOrderSaved, false);
                // Tạo thông báo realtime cho người dùng
                NotificationPayload payload = NotificationPayload.builder()
                                .objectId(consignmentOrderSaved.getId().toString()) // là id của order, thanh toán, ...
                                .accountId(consignmentOrderSaved.getDeliveryPerson().getId())
                                .message("Bạn có đơn vận chuyển mới cần xác nhận!") // nội dung thông báo
                                .type(NotificationPayload.TYPE_CONSIGNMENT_ORDER) // loại thông báo theo objectId
                                // (order, payment, //
                                // ...)
                                .build();
                notificationService.callCreateNotification(payload);
                return response;
        }

        public List<ProductImages> saveProductImages(
                        Product product,
                        MultipartFile[] images) {
                List<ProductImages> productImages = new ArrayList<>();
                for (MultipartFile file : images) {
                        ResponseDocumentDto fileSaved = FilesHelp.saveFile(
                                        file,
                                        product.getId(),
                                        EntityFileType.PRODUCT);
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
                System.out.println(
                                productImages.get(0).getImage().getFileDownloadUri());
                return productImagesRepository.saveAll(productImages);
        }

        public ProductResponse updateProduct(
                        Long id,
                        ProductRequest productRequest) {
                Account account = accountServiceUtill.getAccount();
                List<RoleAccount> allRole = roleAccountRepository.findByAccount(
                                account);
                boolean checkRole = false;
                for (RoleAccount roleAccount : allRole) {
                        if (roleAccount.getRole().getId().equals(Role.ROLE_STAFF)
                                        || roleAccount.getRole().getId().equals(Role.ROLE_STORE_MANAGER) ||
                                        roleAccount.getRole().getId().equals(Role.ROLE_PRODUCT_OWNER)) {
                                checkRole = true;
                        }
                }
                if (!checkRole) {
                        throw new AppException(ErrorCode.LOGIN_ROLE_REQUIRED);
                }
                Product product = productRepository
                                .findById(id)
                                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

                Store store = product.getStore();
                for (RoleAccount roleAccount : allRole) {
                        if (roleAccount.getRole().getId().equals(Role.ROLE_STAFF)
                                        || roleAccount.getRole().getId().equals(Role.ROLE_STORE_MANAGER)) {
                                if (store == null || !isManagerOfStore(account, store)) {
                                        throw new AppException(ErrorCode.NO_MANAGEMENT_RIGHTS);
                                }
                        }
                        if (roleAccount.getRole().getId().equals(Role.ROLE_PRODUCT_OWNER)) {
                                if (product.getOwnerId() == null) {
                                        throw new AppException(ErrorCode.NO_MANAGEMENT_RIGHTS);
                                }
                                if (product.getOwnerId().getId() != account.getId()) {
                                        throw new AppException(ErrorCode.NO_MANAGEMENT_RIGHTS);
                                }
                        }
                }

                product.setName(productRequest.getName());
                product.setPrice(productRequest.getPrice());
                product.setQuantity(productRequest.getQuantity());
                product.setDescription(productRequest.getDescription());

                if (productRequest.getCategoryId() != null) {
                        Category category = categoryRepository
                                        .findById(productRequest.getCategoryId())
                                        .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));
                        product.setCategory(category);
                }
                return convertToResponse(productRepository.save(product), true);
        }

        private boolean isManagerOfStore(Account account, Store store) {
                return store
                                .getAccountStores()
                                .stream()
                                .anyMatch(accountStore -> accountStore.getAccount().equals(account));
        }

        public Page<ProductResponse> getConsignmentProductsByStoreAndState(
                        Long stateProductId,
                        int page,
                        int size) {
                String email = SecurityContextHolder.getContext()
                                .getAuthentication()
                                .getName();
                Account account = accountRepository
                                .findByEmail(email)
                                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

                Store store = accountStoreRepository
                                .findByAccount(account)
                                .orElseThrow(() -> new AppException(ErrorCode.STORE_NOT_FOUND))
                                .getStore();

                Long stateProductFilter = (stateProductId == null ||
                                !stateProductId.equals(StateProduct.DELYVERING))
                                                ? null
                                                : stateProductId;
                Page<Product> products = productRepository.findConsignmentProductsByStoreAndState(
                                store,
                                stateProductFilter,
                                Product.TYPE_PRODUCT_OF_ACCOUNT,
                                PageRequest.of(page, size));
                Page<ProductResponse> productResponses = products.map(product -> {
                        return convertToResponse(product, false);
                });
                return productResponses;
        }

        public Page<ProductResponse> getProductsByCategoryPath(
                        String path,
                        int page,
                        int size) {
                Pageable pageable = PageRequest.of(page, size);
                Category category = categoryRepository
                                .findByPath(path)
                                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));
                System.out.println(category.getPath());
                Page<Product> products = productRepository.findByPathCategory(
                                category,
                                pageable);
                Page<ProductResponse> productResponses = products.map(product -> {
                        return convertToResponse(product, false);
                });
                return productResponses;
        }

        // nv xem san pham theo store
        public Page<ProductResponse> getAllProductsInStore(Pageable pageable) {
                Account account = accountServiceUtill.getAccount();

                Store store = accountStoreRepository
                                .findByAccount(account)
                                .orElseThrow(() -> new AppException(ErrorCode.STORE_NOT_FOUND))
                                .getStore();

                Page<Product> products = productRepository.findAllProductsInStore(
                                store,
                                pageable);
                Page<ProductResponse> productResponses = products.map(product -> {
                        return convertToResponse(product, false);
                });
                return productResponses;
        }

        // kh xem san pham theo store
        public StoreResponse getAllProductsInStoreKH(
                        Long storeId,
                        Pageable pageable) {
                Store store = storeRepository
                                .findById(storeId)
                                .orElseThrow(() -> {
                                        throw new AppException(ErrorCode.STORE_NOT_FOUND);
                                });
                Page<Product> products = productRepository.findAllProductsInStoreKH(
                                store,
                                pageable);
                StoreResponse storeResponse = StoreResponse.builder()
                                .name(store.getName())
                                .streetAddress(getStoreAddress(store))
                                .postalCode(store.getPostalCode())
                                .phone(store.getPhone())
                                .email(store.getEmail())
                                .description(store.getDescription())
                                .build();
                if (store.getImage() != null) {
                        storeResponse.setUrlImage(store.getImage().getFileDownloadUri());
                }
                Page<ProductResponse> productResponses = products.map(product -> {
                        ProductResponse response = convertToResponse(product, false);
                        return response;
                });
                storeResponse.setProduct(productResponses);
                return storeResponse;
        }

        // loc san pham
        public Page<ProductResponse> findProductByCondition(
                        Long categoryId,
                        Long storeId,
                        Long minPrice,
                        Long maxPrice,
                        Pageable pageable) {
                Page<Product> products = productRepository.findProductByCondition(
                                categoryId,
                                storeId,
                                minPrice,
                                maxPrice,
                                pageable);
                Page<ProductResponse> productResponses = products.map(product -> {
                        return convertToResponse(product, false);
                });
                return productResponses;
        }

        public Page<ProductResponse> findProductByOwnerId(int size, int page) {
                String email = SecurityContextHolder.getContext().getAuthentication().getName();
                Account account = accountRepository.findByEmail(email)
                                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
                Pageable pageable = PageRequest.of(page, size);
                Page<Product> product = productRepository.findProductsByOwnerIdAndState(account.getId(), pageable);
                Page<ProductResponse> productResponses = product.map(products -> {
                        return convertToResponse(products, false);
                });
                return productResponses;
        }

}
