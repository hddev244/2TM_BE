package store.chikendev._2tm.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import store.chikendev._2tm.dto.responce.BillOfLadingResponse;
import store.chikendev._2tm.entity.Account;
import store.chikendev._2tm.entity.AccountStore;
import store.chikendev._2tm.entity.BillOfLading;
import store.chikendev._2tm.entity.Order;
import store.chikendev._2tm.entity.Role;
import store.chikendev._2tm.entity.StateOrder;
import store.chikendev._2tm.entity.Store;
import store.chikendev._2tm.exception.AppException;
import store.chikendev._2tm.exception.ErrorCode;
import store.chikendev._2tm.repository.AccountRepository;
import store.chikendev._2tm.repository.AccountStoreRepository;
import store.chikendev._2tm.repository.BillOfLadingRepository;
import store.chikendev._2tm.repository.OrderRepository;
import store.chikendev._2tm.repository.StateOrderRepository;

@Service
public class BillOfLadingService {

    @Autowired
    BillOfLadingRepository billOfLRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private AccountStoreRepository accountStoreRepository;

    @Autowired
    private StateOrderRepository stateOrderRep;

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
        response.setOrderId(billOfLadings.getOrder().getId());
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

}
