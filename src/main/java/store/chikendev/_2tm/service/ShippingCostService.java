package store.chikendev._2tm.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import store.chikendev._2tm.dto.responce.ShippingCostResponse;
import store.chikendev._2tm.entity.ShippingCost;
import store.chikendev._2tm.entity.Store;
import store.chikendev._2tm.entity.Ward;
import store.chikendev._2tm.exception.AppException;
import store.chikendev._2tm.exception.ErrorCode;
import store.chikendev._2tm.repository.ShippingCostRepository;
import store.chikendev._2tm.repository.StoreRepository;
import store.chikendev._2tm.repository.WardRepository;

@Service
public class ShippingCostService {
    @Autowired
    private ShippingCostRepository shippingCostRepository;
    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private WardRepository wardRepository;

    public ShippingCost createShippingCost(Double cost) {
        ShippingCost shippingCost = new ShippingCost();
        shippingCost.setCost(cost);
        return shippingCostRepository.save(shippingCost);
    }

    public ShippingCost updateShippingCost(Long id, Double cost) {
        ShippingCost shippingCost = shippingCostRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.SHIPPING_COST_NOT_FOUND));
        shippingCost.setCost(cost);
        return shippingCostRepository.save(shippingCost);
    }

    public List<ShippingCostResponse> findAll() {
        List<ShippingCost> shippingCosts = shippingCostRepository.findAll();
        return shippingCosts.stream()
                .map(this::convertToResponse)
                .toList();
    }

    private ShippingCostResponse convertToResponse(ShippingCost shippingCost) {
        ShippingCostResponse response = new ShippingCostResponse();
        response.setId(shippingCost.getId());
        response.setCost(shippingCost.getCost());
        return response;
    }

    public ShippingCostResponse getById(Long id) {
        ShippingCost shippingCost = shippingCostRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.SHIPPING_COST_NOT_FOUND));
        return convertToResponse(shippingCost);
    }

    public ShippingCostResponse findShippingCost(Long storeId, Long wardIdDelivery) {
        Store store = storeRepository.findById(storeId).orElseThrow(() -> {
            return new AppException(ErrorCode.STORE_NOT_FOUND);
        });

        Ward wardStore = store.getWard();
        Ward wardDelivery = wardRepository.findById(wardIdDelivery).orElseThrow(() -> {
            return new AppException(ErrorCode.WARD_NOT_FOUND);
        });
        if (wardStore.getId() == wardDelivery.getId()) {
            ShippingCost shippingCost = shippingCostRepository.findById(ShippingCost.IN_THE_WARD).get();
            return convertToResponse(shippingCost);
        }
        if (wardStore.getDistrict().getId() == wardDelivery.getDistrict().getId()) {
            ShippingCost shippingCost = shippingCostRepository.findById(ShippingCost.IN_THE_DISTRICT).get();
            return convertToResponse(shippingCost);
        }
        ShippingCost shippingCost = shippingCostRepository.findById(ShippingCost.OUTSIDE_THE_DISTRICT).get();
        return convertToResponse(shippingCost);
    }

}
