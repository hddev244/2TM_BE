package store.chikendev._2tm.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import store.chikendev._2tm.dto.responce.AccountResponse;
import store.chikendev._2tm.dto.responce.BillOfLadingResponse;
import store.chikendev._2tm.dto.responce.OrderDetailResponse;
import store.chikendev._2tm.dto.responce.OrderResponse;
import store.chikendev._2tm.dto.responce.ProductResponse;
import store.chikendev._2tm.dto.responce.ResponseDocumentDto;
import store.chikendev._2tm.dto.responce.RoleResponse;
import store.chikendev._2tm.entity.Account;
import store.chikendev._2tm.entity.AccountStore;
import store.chikendev._2tm.entity.Address;
import store.chikendev._2tm.entity.BillOfLading;
import store.chikendev._2tm.entity.Image;
import store.chikendev._2tm.entity.Order;
import store.chikendev._2tm.entity.OrderDetails;
import store.chikendev._2tm.entity.Product;
import store.chikendev._2tm.entity.Role;
import store.chikendev._2tm.entity.StateOrder;
import store.chikendev._2tm.entity.Store;
import store.chikendev._2tm.exception.AppException;
import store.chikendev._2tm.exception.ErrorCode;
import store.chikendev._2tm.repository.AccountRepository;
import store.chikendev._2tm.repository.AccountStoreRepository;
import store.chikendev._2tm.repository.BillOfLadingRepository;
import store.chikendev._2tm.repository.ImageRepository;
import store.chikendev._2tm.repository.OrderRepository;
import store.chikendev._2tm.repository.RoleAccountRepository;
import store.chikendev._2tm.repository.StateOrderRepository;
import store.chikendev._2tm.utils.EntityFileType;
import store.chikendev._2tm.utils.FilesHelp;
import store.chikendev._2tm.utils.dtoUtil.response.ImageDtoUtil;

@Service
public class BillOfLadingService {

    @Autowired
    BillOfLadingRepository billOfLRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private RoleAccountRepository roleAccountRepository;

    @Autowired
    private ModelMapper mapper;

    @Autowired
    private AccountStoreRepository accountStoreRepository;

    @Autowired
    private StateOrderRepository stateOrderRep;

    @Autowired
    private ImageRepository imageRepository;

    public BillOfLadingResponse addBillOfLading(Long idOrder) {
        BillOfLading bill = new BillOfLading();
        if (idOrder != null) {
            Order order = orderRepository.findById(idOrder)
                    .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));

            if (order.getStateOrder().getId() != StateOrder.CONFIRMED) {
                throw new AppException(ErrorCode.ORDER_NOT_CONFIRMED);
            }
            bill.setOrder(order);
        }
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Account createBy = accountRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        Store store = accountStoreRepository.findByAccount(createBy)
                .orElseThrow(() -> new AppException(ErrorCode.STORE_NOT_FOUND)).getStore();
        List<AccountStore> accounts = accountStoreRepository.findByStore(store);
        accounts.forEach(deliveryPerson -> {
            deliveryPerson.getAccount().getRoles().forEach(roles -> {
                if (roles.getRole().getId().equals(Role.ROLE_SHIPPER)) {
                    bill.setDeliveryPerson(roles.getAccount());
                }
            });
        });
        if (bill.getDeliveryPerson() == null) {
            throw new AppException(ErrorCode.DELIVERY_PERSON_EMPTY);
        }
        bill.setCreateBy(createBy);
        BillOfLading save = billOfLRepository.save(bill);
        BillOfLadingResponse response = getResponse(save);
        return response;
    }

    public List<BillOfLading> getAllBillOfLadings() {
        return billOfLRepository.findAll();
    }

    public List<BillOfLadingResponse> getBillOfLadingByDeliveryPersonId(String id) {
        List<BillOfLading> billOfLadings = billOfLRepository.getBillOfLadingByDeliveryPersonId(id);
        return billOfLadings.stream().map(this::getResponse).collect(Collectors.toList());
    }

    public BillOfLadingResponse getResponse(BillOfLading billOfLadings) {
        BillOfLadingResponse response = new BillOfLadingResponse();
        response.setId(billOfLadings.getId());
        response.setDeliveryPerson(billOfLadings.getDeliveryPerson().getFullName());
        response.setCreateBy(billOfLadings.getCreateBy().getFullName());
        response.setTotalAmount(getTotalAmount(billOfLadings));
        response.setOrderer(accountResponse(billOfLadings.getOrder().getAccount()));
        response.setOrderId(convertToOrderResponse(billOfLadings.getOrder()));
        response.setCreatedAt(billOfLadings.getCreatedAt());
        if (billOfLadings.getImage() != null) {
            response.setUrlImage(billOfLadings.getImage().getFileDownloadUri());
        }
        return response;
    }

    private Page<BillOfLadingResponse> convertToResponse(Page<BillOfLading> response) {
        return response.map(responses -> {
            return getResponse(responses);
        });
    }

    public Page<BillOfLadingResponse> getByDeliveryPersonIdAndStateId(int size, int page, Long stateId) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Account account = accountRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        Pageable pageable = PageRequest.of(page, size);
        if (stateId == null) {
            Page<BillOfLading> responses = billOfLRepository.findByDeliveryPerson(account, pageable);
            System.out.println(responses);
            return convertToResponse(responses);
        } else {
            System.out.println(stateId + " TC");
            StateOrder stateOrder = stateOrderRep.findById(stateId)
                    .orElseThrow(() -> new AppException(ErrorCode.STATE_NOT_FOUND));
            Page<BillOfLading> responses = billOfLRepository.getByDaliveryPersonIdAndStateId(account, stateOrder,
                    pageable);
            return convertToResponse(responses);
        }
    }

    public String updateStatus(Long idBillOfLading, Long idStateOrder, MultipartFile file) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Account account = accountRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        BillOfLading billOfLading = billOfLRepository.findById(idBillOfLading)
                .orElseThrow(() -> new AppException(ErrorCode.BILL_OF_LADING_NOT_FOUND));
        if (!billOfLading.getDeliveryPerson().getId().equals(account.getId())) {
            throw new AppException(ErrorCode.ROLE_ERROR);
        }
        StateOrder stateOrder = stateOrderRep.findById(idStateOrder).orElseThrow(() -> {
            return new AppException(ErrorCode.STATE_ORDER_NOT_FOUND);
        });
        if (stateOrder.getId() == StateOrder.ORDER_RETRIEVING) {
            if (billOfLading.getOrder().getStateOrder().getId() != StateOrder.CONFIRMED) {
                throw new AppException(ErrorCode.INVALID_STATUS_CHANGE);
            }
            updateStatusOrder(billOfLading, stateOrder, file);
        } else if (stateOrder.getId() == StateOrder.ORDER_SUCCESSFULLY_RETRIEVED) {
            if (billOfLading.getOrder().getStateOrder().getId() != StateOrder.ORDER_RETRIEVING) {
                throw new AppException(ErrorCode.INVALID_STATUS_CHANGE);
            }
            updateStatusOrder(billOfLading, stateOrder, file);
        } else if (stateOrder.getId() == StateOrder.DELIVERING) {
            if (billOfLading.getOrder().getStateOrder().getId() != StateOrder.ORDER_SUCCESSFULLY_RETRIEVED) {
                throw new AppException(ErrorCode.INVALID_STATUS_CHANGE);
            }
            updateStatusOrder(billOfLading, stateOrder, file);
        } else if (stateOrder.getId() == StateOrder.DELIVERED_SUCCESS
                || stateOrder.getId() == StateOrder.DELIVERED_FAIL) {
            if (billOfLading.getOrder().getStateOrder().getId() != StateOrder.DELIVERING) {
                throw new AppException(ErrorCode.INVALID_STATUS_CHANGE);
            }
            updateStatusOrder(billOfLading, stateOrder, file);
        } else if (stateOrder.getId() == StateOrder.RETURNED) {
            if (billOfLading.getOrder().getStateOrder().getId() != StateOrder.ORDER_RETURN) {
                throw new AppException(ErrorCode.INVALID_STATUS_CHANGE);
            }
            updateStatusOrder(billOfLading, stateOrder, file);
        } else if (stateOrder.getId() == StateOrder.RETURNED_SUCCESS) {
            if (billOfLading.getOrder().getStateOrder().getId() != StateOrder.RETURNED) {
                throw new AppException(ErrorCode.INVALID_STATUS_CHANGE);
            }
            updateStatusOrder(billOfLading, stateOrder, file);
        } else {
            throw new AppException(ErrorCode.INVALID_STATUS_CHANGE);
        }

        return "Cập nhật trạng thái thành công";
    }

    private void updateStatusOrder(BillOfLading billOfLading, StateOrder stateOrder, MultipartFile file) {
        Order order = billOfLading.getOrder();
        if (stateOrder.getId() == StateOrder.DELIVERED_SUCCESS) {
            if (file == null) {
                throw new AppException(ErrorCode.FILE_NOT_FOUND);
            }
            ResponseDocumentDto fileSaved = FilesHelp.saveFile(
                    file,
                    billOfLading.getId(),
                    EntityFileType.BILL_OF_LANDING);
            Image image = Image.builder()
                    .fileId(fileSaved.getFileId())
                    .fileName(fileSaved.getFileName())
                    .fileDownloadUri(fileSaved.getFileDownloadUri())
                    .fileType(fileSaved.getFileType())
                    .size(fileSaved.getSize())
                    .build();
            Image imageSaved = imageRepository.saveAndFlush(image);
            billOfLading.setImage(imageSaved);
        }
        order.setStateOrder(stateOrder);
        orderRepository.save(order);
    }

    public String getAddress(Address address) {
        if (address == null) {
            return "";
        }
        if (address.getWard() != null) {
            String addressWard = address.getWard().getName();
            String addressDistrict = address.getWard().getDistrict().getName();
            String addressProvince = address
                    .getWard()
                    .getDistrict()
                    .getProvinceCity()
                    .getName();
            String addressAddress = address.getStreetAddress() == null
                    ? ""
                    : address.getStreetAddress() + ", ";
            return (addressAddress +
                    addressWard +
                    ", " +
                    addressDistrict +
                    ", " +
                    addressProvince);
        }
        return "";
    }

    public AccountResponse accountResponse(Account account) {
        ResponseDocumentDto image = FilesHelp.getOneDocument(
                account.getId(),
                EntityFileType.USER_AVATAR);
        return AccountResponse.builder()
                .id(account.getId())
                .username(account.getUsername())
                .fullName(account.getFullName())
                .phoneNumber(account.getPhoneNumber())
                .email(account.getEmail())
                .roles(
                        roleAccountRepository
                                .findByAccount(account)
                                .stream()
                                .map(roleAccount -> mapper.map(roleAccount.getRole(),
                                        RoleResponse.class))
                                .toList())
                .address(getAddress(account.getAddress()))
                .violationPoints(account.getViolationPoints())
                .createdAt(account.getCreatedAt())
                .updatedAt(account.getUpdatedAt())
                .stateName(account.getState().getName())
                .image(image)
                .build();
    }

    public Double getTotalAmount(BillOfLading billOfLading) {
        if (billOfLading.getOrder().getPaymentMethod().getId() == 1) {
            Double amount = billOfLading.getOrder().getTotalPrice() + billOfLading.getOrder().getShippingCost();
            return amount;
        } else {
            Double amount = 0.0;
            return amount;
        }
    }

    private String getAddress(Order order) {
        if (order == null) {
            return "";
        }
        if (order.getWard() != null) {
            String addressWard = order.getWard().getName();
            String addressDistrict = order.getWard().getDistrict().getName();
            String addressProvince = order.getWard().getDistrict().getProvinceCity().getName();
            String addressAddress = order.getConsigneeDetailAddress() == null ? ""
                    : order.getConsigneeDetailAddress() + ", ";
            return addressAddress + addressWard + ", " + addressDistrict + ", " +
                    addressProvince;
        }
        return "";

    }

    private OrderDetailResponse convertToOrderDetailResponse(OrderDetails detail) {
        return OrderDetailResponse.builder()
                .id(detail.getId())
                .price(detail.getPrice())
                .quantity(detail.getQuantity())
                .product(convertToProductResponse(detail.getProduct()))
                .build();
    }

    private ProductResponse convertToProductResponse(Product product) {
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
            return ImageDtoUtil.convertToImageResponse(image);
        }).toList();
        response.setImages(responseDocument);
        return response;
    }

    private OrderResponse convertToOrderResponse(Order order) {
        List<OrderDetails> details = order.getDetails();
        List<OrderDetailResponse> orderDetailResponses = new ArrayList<>();
        if (details != null) {
            orderDetailResponses = details.stream().map(detail -> {
                return convertToOrderDetailResponse(detail);
            }).collect(Collectors.toList());
        }

        String storeName = (order.getStore() != null) ? order.getStore().getName() : "";

        return OrderResponse.builder()
                .id(order.getId())
                .deliveryCost(order.getDeliveryCost())
                .note(order.getNote())
                .createdAt(order.getCreatedAt())
                .completeAt(order.getCompleteAt())
                .paymentStatus(order.getPaymentStatus())
                .paymentId(order.getPaymentId())
                .address(getAddress(order))
                .consigneeName(order.getConsigneeName())
                .consigneePhoneNumber(order.getConsigneePhoneNumber())
                .totalPrice(order.getTotalPrice())
                .accountName(order.getAccount() != null ? order.getAccount().getFullName() : "")
                .state(order.getStateOrder() != null ? order.getStateOrder().getStatus() : "")
                .paymentMethodName(order.getPaymentMethod() != null ? order.getPaymentMethod().getName() : "")
                .detail(orderDetailResponses)
                .storeName(storeName)
                .paymentRecordId(order.getPaymentRecord() != null ? order.getPaymentRecord().getId() : "")
                .build();
    }

}
