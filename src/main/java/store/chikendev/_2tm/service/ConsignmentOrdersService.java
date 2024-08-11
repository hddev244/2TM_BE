package store.chikendev._2tm.service;

import java.util.ArrayList;
import java.util.Date;
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
import store.chikendev._2tm.dto.request.NotificationPayload;
import store.chikendev._2tm.dto.responce.AccountResponse;
import store.chikendev._2tm.dto.responce.ConsignmentOrderStateResponse;
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
import store.chikendev._2tm.entity.ProductCommission;
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
import store.chikendev._2tm.repository.ProductCommissionRepository;
import store.chikendev._2tm.repository.ProductRepository;
import store.chikendev._2tm.repository.StateConsignmentOrderRepository;
import store.chikendev._2tm.repository.StateProductRepository;
import store.chikendev._2tm.repository.StoreRepository;
import store.chikendev._2tm.repository.WardRepository;
import store.chikendev._2tm.utils.EntityFileType;
import store.chikendev._2tm.utils.FilesHelp;
import store.chikendev._2tm.utils.dtoUtil.response.ImageDtoUtil;

@Service
public class ConsignmentOrdersService {

        @Autowired
        private NotificationService notificationService;

        @Autowired
        ImageRepository imageRepository;

        @Autowired
        private ProductRepository productRepository;

        @Autowired
        private AccountRepository accountRepository;

        @Autowired
        private ConsignmentOrdersRepository consignmentOrdersRepository;

        @Autowired
        private StateConsignmentOrderRepository stateConsignmentOrderRepository;

        @Autowired
        private StoreRepository storeRepository;

        @Autowired
        private AccountStoreRepository accountStoreRepository;

        @Autowired
        private CategoryRepository categoryRepository;

        @Autowired
        private StateProductRepository stateProductRepository;

        @Autowired
        private AttributeDetailRepository attributeDetailRepository;

        @Autowired
        private ProductAttributeDetailRepository productAttributeDetailRepository;

        @Autowired
        private WardRepository wardRepository;

        @Autowired
        private ProductCommissionRepository commissionRepository;

        public String createConsignmentOrders(ConsignmentOrdersRequest request, MultipartFile[] files) {
                String email = SecurityContextHolder.getContext().getAuthentication().getName();
                Account account = accountRepository.findByEmail(email).orElseThrow(() -> {
                        throw new AppException(ErrorCode.USER_NOT_FOUND);
                });
                Category category = categoryRepository.findById(request.getIdCategory()).orElseThrow(() -> {
                        throw new AppException(ErrorCode.CATEGORY_NOT_FOUND);
                });
                StateProduct stateProduct = stateProductRepository.findById(StateProduct.IN_CONFIRM).get();
                Store store = storeRepository.findById(request.getStoreId()).orElseThrow(() -> {
                        throw new AppException(ErrorCode.STORE_NOT_FOUND);
                });
                Ward ward = wardRepository.findById(request.getWardId()).orElseThrow(() -> {
                        throw new AppException(ErrorCode.WARD_NOT_FOUND);
                });
                List<ProductCommission> productCommissions = commissionRepository.findAllOrderByCreatedAtDesc();
                Product product = Product.builder()
                                .name(request.getName())
                                .price(request.getPrice())
                                .quantity(request.getQuantity())
                                .description(request.getDescription())
                                .category(category)
                                .state(stateProduct)
                                .store(store)
                                .ownerId(account)
                                .productCommission(productCommissions.get(0))
                                .type(Product.TYPE_PRODUCT_OF_ACCOUNT)
                                .build();
                Product saveProduct = productRepository.save(product);
                // lưu ảnh
                for (MultipartFile file : files) {
                        FilesHelp.saveFile(file, saveProduct.getId(), EntityFileType.PRODUCT);
                }
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

                Optional<Account> deliveryPerson = store.getAccountStores().stream().flatMap(acc -> acc
                                .getAccount().getRoles().stream()
                                .filter(role -> role.getRole().getId().equals("NVGH"))
                                .map(role -> acc.getAccount())).findFirst();
                if (deliveryPerson.isPresent() == false) {
                        throw new AppException(ErrorCode.DELIVERY_PERSON_NOT_FOUND);
                }
                StateConsignmentOrder state = stateConsignmentOrderRepository.findById(StateConsignmentOrder.CREATED)
                                .get();
                ConsignmentOrders save = ConsignmentOrders.builder()
                                .note(request.getNote())
                                .ordererId(account)
                                .product(product)
                                .store(store)
                                .stateId(state)
                                .phoneNumber(request.getPhoneNumber())
                                .detailAddress(request.getDetailAddress())
                                .ward(ward)
                                .deliveryPerson(deliveryPerson.get())
                                .statusChangeDate(new Date())
                                .build();
                return ("Tạo vận đơn thành công, mã đơn hàng của bạn là: " +
                                consignmentOrdersRepository.save(save).getId());
        }

        // tìm theo all hoặc trạng thái của all vai trò
        public Page<ConsignmentOrdersResponse> getByStateOrAll(int size, int page, Long stateId) {
                Pageable pageable = PageRequest.of(page, size);
                String email = SecurityContextHolder.getContext().getAuthentication().getName();
                Account account = accountRepository.findByEmail(email).orElseThrow(() -> {
                        throw new AppException(ErrorCode.USER_NOT_FOUND);
                });
                Optional<AccountStore> accountStore = accountStoreRepository.findByAccount(account);
                if (account.getRoles().stream().anyMatch(role -> role.getRole().getId().equals("NVGH"))) {
                        if (stateId == null) {
                                Page<ConsignmentOrders> response = consignmentOrdersRepository
                                                .findByDeliveryPerson2(account, pageable);
                                return convertToResponse(response);
                        }
                        StateConsignmentOrder state = stateConsignmentOrderRepository.findById(stateId)
                                        .orElseThrow(() -> {
                                                throw new AppException(ErrorCode.STATE_NOT_FOUND);
                                        });
                        Page<ConsignmentOrders> response = consignmentOrdersRepository.findByDeliveryPersonAndStateId(
                                        account,
                                        state,
                                        pageable);
                        return convertToResponse(response);
                } else if (account.getRoles().stream()
                                .anyMatch(role -> role.getRole().getId().equals("CH"))) {
                        if (stateId == null) {
                                Page<ConsignmentOrders> response = consignmentOrdersRepository.findByOrdererId(
                                                account,
                                                pageable);
                                return convertToResponse(response);
                        }
                        StateConsignmentOrder state = stateConsignmentOrderRepository
                                        .findById(stateId)
                                        .orElseThrow(() -> {
                                                throw new AppException(ErrorCode.STATE_NOT_FOUND);
                                        });
                        Page<ConsignmentOrders> response = consignmentOrdersRepository.findByOrdererIdAndStateId(
                                        account,
                                        state,
                                        pageable);
                        return convertToResponse(response);
                } else {
                        // NVCH - QLCH
                        if (stateId == null) {
                                if (accountStore.isPresent()) {
                                        Page<ConsignmentOrders> response = consignmentOrdersRepository.findByStore(
                                                        accountStore.get().getStore(),
                                                        pageable);
                                        return convertToResponse(response);
                                }
                        }
                        StateConsignmentOrder state = stateConsignmentOrderRepository
                                        .findById(stateId)
                                        .orElseThrow(() -> {
                                                throw new AppException(ErrorCode.STATE_NOT_FOUND);
                                        });
                        if (accountStore.isPresent()) {
                                Page<ConsignmentOrders> response = consignmentOrdersRepository.findByStoreAndStateId(
                                                accountStore.get().getStore(),
                                                state,
                                                pageable);
                                return convertToResponse(response);
                        }
                        throw new AppException(ErrorCode.STORE_NOT_FOUND);
                }
        }

        private Page<ConsignmentOrdersResponse> convertToResponse(
                        Page<ConsignmentOrders> response) {
                return response.map(consignmentOrders -> {
                        return convertToConsignmentOrdersResponse(consignmentOrders);
                });
        }

        public ConsignmentOrdersResponse convertToConsignmentOrdersResponse(
                        ConsignmentOrders consignmentOrders) {
                ConsignmentOrdersResponse response = new ConsignmentOrdersResponse();

                if (consignmentOrders.getStateId() != null) {
                        response.setState(
                                        ConsignmentOrderStateResponse.builder()
                                                        .id(consignmentOrders.getStateId().getId())
                                                        .status(consignmentOrders.getStateId().getStatus())
                                                        .description(
                                                                        consignmentOrders.getStateId().getDescription())
                                                        .build());
                }

                response.setId(consignmentOrders.getId());
                response.setNote(consignmentOrders.getNote());
                response.setCreatedAt(consignmentOrders.getCreatedAt());
                response.setOrderer(
                                AccountResponse.builder()
                                                .id(consignmentOrders.getOrdererId().getId())
                                                .fullName(consignmentOrders.getOrdererId().getFullName())
                                                .build());
                response.setDeliveryPerson(
                                AccountResponse.builder()
                                                .id(consignmentOrders.getDeliveryPerson().getId())
                                                .fullName(consignmentOrders.getDeliveryPerson().getFullName())
                                                .phoneNumber(
                                                                consignmentOrders.getDeliveryPerson().getPhoneNumber())
                                                .build());
                ResponseDocumentDto thumbnail = null;
                if (consignmentOrders.getProduct().getImages() != null) {
                        thumbnail = consignmentOrders
                                        .getProduct()
                                        .getImages()
                                        .stream()
                                        .map(img -> ImageDtoUtil.convertToImageResponse(img.getImage()))
                                        .findFirst()
                                        .orElse(null);
                }
                response.setProduct(
                                ProductResponse.builder()
                                                .id(consignmentOrders.getProduct().getId())
                                                .name(consignmentOrders.getProduct().getName())
                                                .price(consignmentOrders.getProduct().getPrice())
                                                .quantity(consignmentOrders.getProduct().getQuantity())
                                                .description(consignmentOrders.getProduct().getDescription())
                                                .thumbnail(thumbnail)
                                                .build());
                response.setStore(
                                StoreResponse.builder()
                                                .id(consignmentOrders.getStore().getId())
                                                .name(consignmentOrders.getStore().getName())
                                                .build());
                response.setStateName(consignmentOrders.getStateId().getStatus());
                response.setAddress(getAddress(consignmentOrders));
                response.setPhone(consignmentOrders.getPhoneNumber());
                response.setStatusChangeDate(consignmentOrders.getStatusChangeDate());
                response.setImage(
                                consignmentOrders.getImage() == null
                                                ? null
                                                : ImageDtoUtil.convertToImageResponse(
                                                                consignmentOrders.getImage()));
                return response;
        }

        private String getAddress(ConsignmentOrders consignmentOrders) {
                if (consignmentOrders == null) {
                        return "";
                }
                if (consignmentOrders.getWard() != null) {
                        String addressWard = consignmentOrders.getWard().getName();
                        String addressDistrict = consignmentOrders
                                        .getWard()
                                        .getDistrict()
                                        .getName();
                        String addressProvince = consignmentOrders
                                        .getWard()
                                        .getDistrict()
                                        .getProvinceCity()
                                        .getName();
                        String addressAddress = consignmentOrders.getDetailAddress() == null
                                        ? ""
                                        : consignmentOrders.getDetailAddress() + ", ";
                        return (addressAddress +
                                        addressWard +
                                        ", " +
                                        addressDistrict +
                                        ", " +
                                        addressProvince);
                }
                return "";
        }

        // cập nhật trạng thái giao hàng - NVGH
        public String updateStatus(Long idStatus, Long idConsignmentOrders, MultipartFile file) {
                String email = SecurityContextHolder.getContext().getAuthentication().getName();
                Account account = accountRepository.findByEmail(email).orElseThrow(() -> {
                        throw new AppException(ErrorCode.USER_NOT_FOUND);
                });
                ConsignmentOrders consignmentOrders = consignmentOrdersRepository.findById(idConsignmentOrders)
                                .orElseThrow(() -> {
                                        throw new AppException(ErrorCode.CONSIGNMENT_ORDER_NOT_FOUND);
                                });
                StateConsignmentOrder state = stateConsignmentOrderRepository.findById(idStatus).orElseThrow(() -> {
                        throw new AppException(ErrorCode.STATE_NOT_FOUND);
                });
                if (state.getId() == StateConsignmentOrder.CANCEL || state.getId() == StateConsignmentOrder.COMPLETED
                                || state.getId() == StateConsignmentOrder.CREATED
                                || state.getId() == StateConsignmentOrder.REFUSE) {
                        throw new AppException(ErrorCode.STATE_ERROR);
                }
                if (consignmentOrders.getProduct().getState().getId() != StateProduct.DELYVERING) {
                        throw new AppException(ErrorCode.STATE_ERROR);
                }
                if (consignmentOrders.getDeliveryPerson().getId().equals(account.getId())) {
                        if (state.getId() == StateConsignmentOrder.PICKED_UP) {
                                if (file == null) {
                                        throw new AppException(ErrorCode.FILE_NOT_FOUND);
                                }
                                ResponseDocumentDto fileSaved = FilesHelp.saveFile(file, consignmentOrders.getId(),
                                                EntityFileType.CONSIGNMENT_ORDER);
                                Image image = Image.builder()
                                                .fileId(fileSaved.getFileId())
                                                .fileName(fileSaved.getFileName())
                                                .fileDownloadUri(fileSaved.getFileDownloadUri())
                                                .fileType(fileSaved.getFileType())
                                                .size(fileSaved.getSize())
                                                .build();
                                Image imageSaved = imageRepository.save(image);
                                consignmentOrders.setImage(imageSaved);
                        }

                        consignmentOrders.setStatusChangeDate(new Date());
                        consignmentOrders.setStateId(state);
                        consignmentOrdersRepository.save(consignmentOrders);

                        // Tạo thông báo realtime cho người dùng
                        String objectId = consignmentOrders.getId().toString();
                        NotificationPayload payload = NotificationPayload.builder()
                                        .objectId(objectId) // là id của order, thanh toán, ...
                                        .accountId(consignmentOrders.getProduct().getOwnerId().getId())
                                        .message(state.getDescription()) // nội dung thông báo
                                        .type(NotificationPayload.TYPE_CONSIGNMENT_ORDER) // loại thông báo theo
                                                                                          // objectId (order, payment,
                                                                                          // // ...)
                                        .build();

                        notificationService.callCreateNotification(payload);
                        return "Cập nhật trạng thái thành công";
                }
                throw new AppException(ErrorCode.NO_MANAGEMENT_RIGHTS);
        }

        // xác nhận hoàn thành đơn hàng ký gửi - NVCH - QLCH
        public String successConsignmentOrders(Long idConsignmentOrders) {
                String email = SecurityContextHolder.getContext().getAuthentication().getName();
                Account account = accountRepository.findByEmail(email).orElseThrow(() -> {
                        throw new AppException(ErrorCode.USER_NOT_FOUND);
                });
                ConsignmentOrders consignmentOrders = consignmentOrdersRepository.findById(idConsignmentOrders)
                                .orElseThrow(() -> {
                                        throw new AppException(ErrorCode.CONSIGNMENT_ORDER_NOT_FOUND);
                                });
                if (consignmentOrders.getProduct().getState().getId() != StateProduct.DELYVERING) {
                        throw new AppException(ErrorCode.STATE_ERROR);
                }
                if (consignmentOrders.getStore().getAccountStores().stream()
                                .anyMatch(acc -> acc.getAccount().getId().equals(account.getId()))) {
                        if (consignmentOrders.getStateId().getId() == StateConsignmentOrder.PICKED_UP) {
                                if (consignmentOrders.getImage() != null) {
                                        StateConsignmentOrder state = stateConsignmentOrderRepository
                                                        .findById(StateConsignmentOrder.COMPLETED).get();
                                        consignmentOrders.setStatusChangeDate(new Date());
                                        consignmentOrders.setStateId(state);

                                        Product product = consignmentOrders.getProduct();
                                        product.setState(stateProductRepository.findById(StateProduct.CONFIRM).get());
                                        product.setAccount(account);
                                        // lưu
                                        productRepository.save(product);
                                        consignmentOrdersRepository.save(consignmentOrders);
                                        return "Xác nhận hoàn thành đơn hàng ký gửi thành công";
                                }
                                throw new AppException(ErrorCode.FILE_NOT_FOUND);
                        }
                        throw new AppException(ErrorCode.STATE_ERROR);
                }
                throw new AppException(ErrorCode.NO_MANAGEMENT_RIGHTS);
        }

        // xác nhận đơn hàng ký gửi - NVCH - QLCH
        public String confirmOrder(Long idConsignmentOrders) {
                String email = SecurityContextHolder.getContext().getAuthentication().getName();
                Account account = accountRepository.findByEmail(email).orElseThrow(() -> {
                        throw new AppException(ErrorCode.USER_NOT_FOUND);
                });
                ConsignmentOrders consignmentOrders = consignmentOrdersRepository.findById(idConsignmentOrders)
                                .orElseThrow(() -> {
                                        throw new AppException(ErrorCode.CONSIGNMENT_ORDER_NOT_FOUND);
                                });
                if (consignmentOrders.getStateId().getId() != StateConsignmentOrder.CREATED) {
                        throw new AppException(ErrorCode.STATE_ERROR);
                }
                Product product = consignmentOrders.getProduct();
                if (product.getState().getId() != StateProduct.IN_CONFIRM) {
                        throw new AppException(ErrorCode.STATE_ERROR);
                }
                if (consignmentOrders.getStore().getAccountStores().stream()
                                .anyMatch(acc -> acc.getAccount().getId().equals(account.getId()))) {
                        product.setState(stateProductRepository.findById(StateProduct.DELYVERING).get());
                        productRepository.save(product);
                        return "Xác nhận đơn ký gửi thành công";
                }
                throw new AppException(ErrorCode.NO_MANAGEMENT_RIGHTS);
        }

        // từ chối đơn hàng ký gửi - NVCH - QLCH
        public String refuseOrder(Long idConsignmentOrders) {
                String email = SecurityContextHolder.getContext().getAuthentication().getName();
                Account account = accountRepository.findByEmail(email).orElseThrow(() -> {
                        throw new AppException(ErrorCode.USER_NOT_FOUND);
                });
                ConsignmentOrders consignmentOrders = consignmentOrdersRepository.findById(idConsignmentOrders)
                                .orElseThrow(() -> {
                                        throw new AppException(ErrorCode.CONSIGNMENT_ORDER_NOT_FOUND);
                                });
                if (consignmentOrders.getStateId().getId() != StateConsignmentOrder.CREATED) {
                        throw new AppException(ErrorCode.STATE_ERROR);
                }
                Product product = consignmentOrders.getProduct();
                if (product.getState().getId() != StateProduct.IN_CONFIRM) {
                        throw new AppException(ErrorCode.STATE_ERROR);
                }
                if (consignmentOrders.getStore().getAccountStores().stream()
                                .anyMatch(acc -> acc.getAccount().getId().equals(account.getId()))) {
                        product.setState(stateProductRepository.findById(StateProduct.REFUSE).get());
                        consignmentOrders.setStateId(
                                        stateConsignmentOrderRepository.findById(StateConsignmentOrder.REFUSE).get());
                        productRepository.save(product);
                        return "Từ chối đơn ký gửi thành công";
                }
                throw new AppException(ErrorCode.NO_MANAGEMENT_RIGHTS);
        }

        public ConsignmentOrdersResponse getConsignmentOrderById(Long id) {
                ConsignmentOrders consignmentOrder = consignmentOrdersRepository
                                .findById(id)
                                .orElseThrow(() -> new AppException(ErrorCode.CONSIGNMENT_ORDER_NOT_FOUND));

                return convertToConsignmentOrdersResponse(consignmentOrder);
        }

        public Page<ConsignmentOrdersResponse> getByStateOrAllWithOwner(
                        int size,
                        int page,
                        Long stateId) {
                String email = SecurityContextHolder.getContext()
                                .getAuthentication()
                                .getName();
                Account account = accountRepository
                                .findByEmail(email)
                                .orElseThrow(() -> {
                                        throw new AppException(ErrorCode.USER_NOT_FOUND);
                                });

                Pageable pageable = PageRequest.of(page, size);

                if (stateId == null) {
                        Page<ConsignmentOrders> response = consignmentOrdersRepository.getByStateOrAllWithOwner(
                                        account,
                                        pageable);
                        return convertToResponse(response);
                } else {
                        StateConsignmentOrder state = stateConsignmentOrderRepository
                                        .findById(stateId)
                                        .orElseThrow(() -> {
                                                throw new AppException(ErrorCode.STATE_NOT_FOUND);
                                        });
                        Page<ConsignmentOrders> response = consignmentOrdersRepository.getByStateOrAllWithOwner(
                                        account,
                                        state,
                                        pageable);
                        return convertToResponse(response);
                }
        }

        // CH hủy yêu cầu ký gửi
        public void cancelConsignmentOrder(Long id) {
                String email = SecurityContextHolder.getContext().getAuthentication().getName();
                Account account = accountRepository.findByEmail(email)
                                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

                ConsignmentOrders consignmentOrder = consignmentOrdersRepository.findById(id)
                                .orElseThrow(() -> new AppException(ErrorCode.CONSIGNMENT_ORDER_NOT_FOUND));

                if (!consignmentOrder.getOrdererId().getId().equals(account.getId())) {
                        throw new AppException(ErrorCode.NO_MANAGEMENT_RIGHTS);
                }

                Long currentState = consignmentOrder.getStateId().getId();
                if (!currentState.equals(StateConsignmentOrder.CREATED)) {
                        throw new AppException(ErrorCode.STATE_ERROR);
                }

                StateConsignmentOrder cancelState = stateConsignmentOrderRepository
                                .findById(StateConsignmentOrder.CANCEL)
                                .orElseThrow(() -> new AppException(ErrorCode.STATE_NOT_FOUND));
                consignmentOrder.setStateId(cancelState);
                Product product = consignmentOrder.getProduct();
                StateProduct canceledState = stateProductRepository.findById(StateProduct.CANCELED)
                                .orElseThrow(() -> new AppException(ErrorCode.STATE_NOT_FOUND));
                product.setState(canceledState);
                productRepository.save(product);
                consignmentOrdersRepository.save(consignmentOrder);
        }

        public ConsignmentOrdersResponse getConsignmentOrders(Long id) {
                String email = SecurityContextHolder.getContext().getAuthentication().getName();
                Account account = accountRepository.findByEmail(email)
                                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
                ConsignmentOrders consignmentOrder = consignmentOrdersRepository.findById(id)
                                .orElseThrow(() -> new AppException(ErrorCode.CONSIGNMENT_ORDER_NOT_FOUND));
                if (account.getId() == consignmentOrder.getOrdererId().getId()) {
                        return convertToConsignmentOrdersResponse(consignmentOrder);
                } else {
                        throw new AppException(ErrorCode.NO_MANAGEMENT_RIGHTS);
                }

        }
}
