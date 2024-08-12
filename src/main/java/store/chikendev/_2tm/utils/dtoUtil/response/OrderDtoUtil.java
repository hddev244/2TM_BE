package store.chikendev._2tm.utils.dtoUtil.response;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import store.chikendev._2tm.dto.responce.OrderDetailResponse;
import store.chikendev._2tm.dto.responce.OrderResponse;
import store.chikendev._2tm.dto.responce.ProductResponse;
import store.chikendev._2tm.dto.responce.ResponseDocumentDto;
import store.chikendev._2tm.entity.Image;
import store.chikendev._2tm.entity.Order;
import store.chikendev._2tm.entity.OrderDetails;
import store.chikendev._2tm.entity.Product;

@Service
public class OrderDtoUtil {

    public OrderDetailResponse convertToOrderDetailResponse(OrderDetails detail) {
        return OrderDetailResponse.builder()
                .id(detail.getId())
                .price(detail.getPrice())
                .quantity(detail.getQuantity())
                .product(convertToProductResponse(detail.getProduct()))
                .build();
    }

    public ProductResponse convertToProductResponse(Product product) {
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

     public OrderResponse convertToOrderResponse(Order order) {
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


}
