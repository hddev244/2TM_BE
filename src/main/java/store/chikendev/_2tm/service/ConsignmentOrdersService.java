package store.chikendev._2tm.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import store.chikendev._2tm.dto.request.ConsignmentOrdersRequest;
import store.chikendev._2tm.dto.responce.ConsignmentOrdersResponse;
import store.chikendev._2tm.entity.Account;
import store.chikendev._2tm.entity.AccountStore;
import store.chikendev._2tm.entity.ConsignmentOrders;
import store.chikendev._2tm.entity.Product;
import store.chikendev._2tm.entity.StateConsignmentOrder;
import store.chikendev._2tm.entity.Store;
import store.chikendev._2tm.exception.AppException;
import store.chikendev._2tm.exception.ErrorCode;
import store.chikendev._2tm.repository.AccountRepository;
import store.chikendev._2tm.repository.AccountStoreRepository;
import store.chikendev._2tm.repository.ConsignmentOrdersRepository;
import store.chikendev._2tm.repository.ProductRepository;
import store.chikendev._2tm.repository.StateConsignmentOrderRepository;
import store.chikendev._2tm.repository.StoreRepository;

@Service
public class ConsignmentOrdersService {

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

    public String createConsignmentOrders(ConsignmentOrdersRequest request) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Account account = accountRepository.findByEmail(email).orElseThrow(() -> {
            throw new AppException(ErrorCode.USER_NOT_FOUND);
        });
        Product product = productRepository.findById(request.getProductId()).orElseThrow(() -> {
            throw new AppException(ErrorCode.PRODUCT_NOT_FOUND);
        });
        Store store = storeRepository.findById(request.getStoreId()).orElseThrow(() -> {
            throw new AppException(ErrorCode.STORE_NOT_FOUND);
        });

        StateConsignmentOrder state = stateConsignmentOrderRepository.findById(StateConsignmentOrder.IN_CONFIRM).get();

        ConsignmentOrders save = ConsignmentOrders.builder()
                .note(request.getNote())
                .ordererId(account)
                .product(product)
                .store(store)
                .stateId(state)
                .build();
        return "Tạo vận đơn thành công, mã đơn hàng của bạn là: " + consignmentOrdersRepository.save(save).getId();
    }

    public Page<ConsignmentOrdersResponse> getByState(int page, int size, Long stateId) {
        Pageable pageable = PageRequest.of(page, size);
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Account account = accountRepository.findByEmail(email).orElseThrow(() -> {
            throw new AppException(ErrorCode.USER_NOT_FOUND);
        });
        StateConsignmentOrder state = stateConsignmentOrderRepository.findById(stateId).orElseThrow(() -> {
            throw new AppException(ErrorCode.STATE_NOT_FOUND);
        });
        Optional<AccountStore> accountStore = accountStoreRepository.findByAccount(account);

        final boolean checkStaff = account.getRoles().stream()
                .anyMatch(role -> role.getRole().getId().equals("NVGH"));
        if (checkStaff) {
            Page<ConsignmentOrders> responce = consignmentOrdersRepository.findByDeliveryPersonAndStateId(account,
                    state, pageable);
            return convertToResponse(responce);
        } else {
            if (accountStore.isPresent()) {
                Page<ConsignmentOrders> responce = consignmentOrdersRepository
                        .findByStoreAndStateId(accountStore.get().getStore(), state, pageable);
                return convertToResponse(responce);
            }
            throw new AppException(ErrorCode.STORE_NOT_FOUND);

        }
    }
    // chưa test

    private Page<ConsignmentOrdersResponse> convertToResponse(Page<ConsignmentOrders> responce) {
        return responce.map(consignmentOrders -> {
            ConsignmentOrdersResponse response = new ConsignmentOrdersResponse();
            response.setId(consignmentOrders.getId());
            response.setNote(consignmentOrders.getNote());
            response.setOrdererName(consignmentOrders.getOrdererId().getFullName());
            response.setProductName(consignmentOrders.getProduct().getName());
            response.setStoreName(consignmentOrders.getStore().getName());
            response.setStateName(consignmentOrders.getStateId().getStatus());
            return response;
        });
    }
}
