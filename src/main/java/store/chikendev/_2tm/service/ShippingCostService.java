package store.chikendev._2tm.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import store.chikendev._2tm.entity.ShippingCost;
import store.chikendev._2tm.exception.AppException;
import store.chikendev._2tm.exception.ErrorCode;
import store.chikendev._2tm.repository.ShippingCostRepository;

@Service
public class ShippingCostService {
    @Autowired
    private ShippingCostRepository shippingCostRepository;

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
}
