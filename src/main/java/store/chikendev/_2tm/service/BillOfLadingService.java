package store.chikendev._2tm.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import store.chikendev._2tm.dto.request.BillOfLadingRequest;
import store.chikendev._2tm.dto.responce.BillOfLadingResponse;
import store.chikendev._2tm.entity.Account;
import store.chikendev._2tm.entity.BillOfLading;
import store.chikendev._2tm.entity.Order;
import store.chikendev._2tm.exception.AppException;
import store.chikendev._2tm.exception.ErrorCode;
import store.chikendev._2tm.repository.AccountRepository;
import store.chikendev._2tm.repository.BillOfLadingRepository;
import store.chikendev._2tm.repository.OrderRepository;

@Service
public class BillOfLadingService {

    @Autowired
    BillOfLadingRepository billOfLRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private AccountRepository accountRepository;

    public BillOfLadingResponse addBillOfLading(BillOfLadingRequest request) {
        BillOfLading billol = new BillOfLading();
        if (request.getOrder() != null) {
            Order order = orderRepository.findById(request.getOrder())
                    .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));
            billol.setOrder(order);
        }
        if (request.getDeliveryPerson() != null) {
            Account deliveryPerson = accountRepository.findById(request.getDeliveryPerson())
                    .orElseThrow(() -> new AppException(ErrorCode.USER_EXISTED));
            billol.setDeliveryPerson(deliveryPerson);
        }
        if (request.getCreateBy() != null) {
            Account createBy = accountRepository.findById(request.getCreateBy())
                    .orElseThrow(() -> new AppException(ErrorCode.USER_EXISTED));
            billol.setCreateBy(createBy);
        }
        BillOfLading save = billOfLRepository.save(billol);
        BillOfLadingResponse response = new BillOfLadingResponse();
        response.setId(save.getId());
        response.setCreateBy(save.getCreateBy().getFullName());
        response.setDeliveryPerson(save.getDeliveryPerson().getFullName());
        response.setOrderId(save.getOrder().getId());
        response.setCreatedAt(save.getCreatedAt());
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

        return response;
    }

}
