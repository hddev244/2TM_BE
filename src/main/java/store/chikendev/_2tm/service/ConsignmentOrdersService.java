package store.chikendev._2tm.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import store.chikendev._2tm.dto.request.ConsignmentOrdersRequest;
import store.chikendev._2tm.dto.responce.ConsignmentOrdersResponse;
import store.chikendev._2tm.dto.responce.ResponseDocumentDto;
import store.chikendev._2tm.entity.Account;
import store.chikendev._2tm.entity.AccountStore;
import store.chikendev._2tm.entity.Category;
import store.chikendev._2tm.entity.ConsignmentOrders;
import store.chikendev._2tm.entity.Product;
import store.chikendev._2tm.entity.ProductAttributeDetail;
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
import store.chikendev._2tm.repository.ProductRepository;
import store.chikendev._2tm.repository.StateConsignmentOrderRepository;
import store.chikendev._2tm.repository.StateProductRepository;
import store.chikendev._2tm.repository.StoreRepository;
import store.chikendev._2tm.repository.WardRepository;
import store.chikendev._2tm.utils.EntityFileType;
import store.chikendev._2tm.utils.FilesHelp;

@Service
public class ConsignmentOrdersService {
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

    public String createConsignmentOrders(ConsignmentOrdersRequest request, MultipartFile[] files) {
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

        Optional<Account> deliveryPerson = store.getAccountStores().stream()
                .flatMap(acc -> acc.getAccount().getRoles().stream()
                        .filter(role -> role.getRole().getId().equals("NVGH"))
                        .map(role -> acc.getAccount()))
                .findFirst();
        if (deliveryPerson.isPresent() == false) {
            throw new AppException(ErrorCode.DELIVERY_PERSON_NOT_FOUND);
        }
        StateConsignmentOrder state = stateConsignmentOrderRepository.findById(StateConsignmentOrder.IN_CONFIRM).get();
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
                .build();
        return "Tạo vận đơn thành công, mã đơn hàng của bạn là: " + consignmentOrdersRepository.save(save).getId();
    }

    public Page<ConsignmentOrdersResponse> getByStateOrAll(int size, int page, Long stateId) {
        Pageable pageable = PageRequest.of(page, size);
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Account account = accountRepository.findByEmail(email).orElseThrow(() -> {
            throw new AppException(ErrorCode.USER_NOT_FOUND);
        });
        Optional<AccountStore> accountStore = accountStoreRepository.findByAccount(account);

        final boolean checkStaff = account.getRoles().stream()
                .anyMatch(role -> role.getRole().getId().equals("NVGH"));
        if (checkStaff) {
            if (stateId == null) {
                Page<ConsignmentOrders> response = consignmentOrdersRepository.findByDeliveryPerson(account, pageable);
                return convertToResponse(response);
            }
            StateConsignmentOrder state = stateConsignmentOrderRepository.findById(stateId).orElseThrow(() -> {
                throw new AppException(ErrorCode.STATE_NOT_FOUND);
            });
            Page<ConsignmentOrders> response = consignmentOrdersRepository.findByDeliveryPersonAndStateId(account,
                    state, pageable);
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
            StateConsignmentOrder state = stateConsignmentOrderRepository.findById(stateId).orElseThrow(() -> {
                throw new AppException(ErrorCode.STATE_NOT_FOUND);
            });
            if (accountStore.isPresent()) {
                Page<ConsignmentOrders> response = consignmentOrdersRepository
                        .findByStoreAndStateId(accountStore.get().getStore(), state, pageable);
                return convertToResponse(response);
            }
            throw new AppException(ErrorCode.STORE_NOT_FOUND);

        }
    }

    private Page<ConsignmentOrdersResponse> convertToResponse(Page<ConsignmentOrders> responce) {
        return responce.map(consignmentOrders -> {
            ResponseDocumentDto file = FilesHelp.getOneDocument(consignmentOrders.getId(),
                    EntityFileType.CONSIGNMENT_ORDER);
            ConsignmentOrdersResponse response = new ConsignmentOrdersResponse();
            response.setId(consignmentOrders.getId());
            response.setNote(consignmentOrders.getNote());
            response.setCreatedAt(consignmentOrders.getCreatedAt());
            response.setOrdererName(consignmentOrders.getOrdererId().getFullName());
            response.setDeliveryPersonName(consignmentOrders.getDeliveryPerson().getFullName());
            response.setProductName(consignmentOrders.getProduct().getName());
            response.setStoreName(consignmentOrders.getStore().getName());
            response.setStateName(consignmentOrders.getStateId().getStatus());
            response.setAddress(getAddress(consignmentOrders));
            response.setPhone(consignmentOrders.getPhoneNumber());
            response.setCompleteAt(consignmentOrders.getCompleteAt());
            response.setUrlImage(file.getFileDownloadUri());
            return response;
        });
    }

    private String getAddress(ConsignmentOrders consignmentOrders) {
        if (consignmentOrders == null) {
            return "";
        }
        if (consignmentOrders.getWard() != null) {
            String addressWard = consignmentOrders.getWard().getName();
            String addressDistrict = consignmentOrders.getWard().getDistrict().getName();
            String addressProvince = consignmentOrders.getWard().getDistrict().getProvinceCity().getName();
            String addressAddress = consignmentOrders.getDetailAddress() == null ? ""
                    : consignmentOrders.getDetailAddress() + ", ";
            return addressAddress + addressWard + ", " + addressDistrict + ", " +
                    addressProvince;
        }
        return "";

    }
}
