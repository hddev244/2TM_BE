package store.chikendev._2tm.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import store.chikendev._2tm.dto.request.ConsignmentOrdersRequest;
import store.chikendev._2tm.entity.Account;
import store.chikendev._2tm.entity.ConsignmentOrders;
import store.chikendev._2tm.entity.Product;
import store.chikendev._2tm.entity.StateConsignmentOrder;
import store.chikendev._2tm.entity.Store;
import store.chikendev._2tm.exception.AppException;
import store.chikendev._2tm.exception.ErrorCode;
import store.chikendev._2tm.repository.AccountRepository;
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
}
