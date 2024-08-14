package store.chikendev._2tm.service;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import store.chikendev._2tm.dto.request.VoucherRequest;
import store.chikendev._2tm.dto.responce.OrderResponse;
import store.chikendev._2tm.dto.responce.VoucherResponse;
import store.chikendev._2tm.entity.Order;
import store.chikendev._2tm.entity.Voucher;
import store.chikendev._2tm.exception.AppException;
import store.chikendev._2tm.exception.ErrorCode;
import store.chikendev._2tm.repository.OrderRepository;
import store.chikendev._2tm.repository.VoucherRepository;

@Service
public class VoucherService {
    @Autowired
    private VoucherRepository voucherRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ModelMapper mapper;

    public VoucherResponse addVoucher(VoucherRequest request) {
        Voucher voucher = new Voucher();
        voucher.setId(request.getId());
        voucher.setDiscountPercentage(request.getDiscountPercentage());
        voucher.setNote(request.getNote());
        voucher.setUsageLimit(request.getUsageLimit());
        voucher.setStatus(request.getStatus());
        voucher.setStartDate(request.getStartDate());
        voucher.setEndDate(request.getEndDate());
        voucher.setMinimumPurchaseAmount(request.getMinimumPurchaseAmount());
        voucher.setMaximumDiscountAmount(request.getMaximumDiscountAmount());
        Voucher save = voucherRepository.save(voucher);
        return getResponse(save);
    }

    public VoucherResponse getResponse(Voucher voucher) {
        List<Order> order = orderRepository.findByVoucher(voucher);
        List<OrderResponse> orderResponses = order.stream().map(orders -> mapper.map(orders, OrderResponse.class))
                .toList();
        VoucherResponse response = new VoucherResponse();
        response.setId(voucher.getId());
        response.setDiscountPercentage(voucher.getDiscountPercentage());
        response.setNote(voucher.getNote());
        response.setUsageLimit(voucher.getUsageLimit());
        response.setStatus(voucher.getStatus());
        response.setStartDate(voucher.getStartDate());
        response.setEndDate(voucher.getEndDate());
        response.setMinimumPurchaseAmount(voucher.getMinimumPurchaseAmount());
        response.setMaximumDiscountAmount(voucher.getMaximumDiscountAmount());
        response.setOrders(orderResponses);
        return response;
    }

    public VoucherResponse updateVoucher(VoucherRequest request) {
        Voucher voucher = voucherRepository.findById(request.getId())
                .orElseThrow(() -> new AppException(ErrorCode.VOUCHER_NOT_FOUND));
        voucher.setDiscountPercentage(request.getDiscountPercentage());
        voucher.setNote(request.getNote());
        voucher.setUsageLimit(request.getUsageLimit());
        voucher.setStatus(request.getStatus());
        voucher.setStartDate(request.getStartDate());
        voucher.setEndDate(request.getEndDate());
        voucher.setMinimumPurchaseAmount(request.getMinimumPurchaseAmount());
        voucher.setMaximumDiscountAmount(request.getMaximumDiscountAmount());
        Voucher save = voucherRepository.save(voucher);
        return getResponse(save);
    }

    public List<VoucherResponse> getAll() {
        List<Voucher> voucher = voucherRepository.findAll();
        return voucher.stream().map(this::getResponse).collect(Collectors.toList());
    }

    public VoucherResponse getById(String id) {
        Voucher voucher = voucherRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.VOUCHER_NOT_FOUND));
        return getResponse(voucher);
    }
}
