package store.chikendev._2tm.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import store.chikendev._2tm.entity.Product;
import store.chikendev._2tm.entity.ProductCommission;
import store.chikendev._2tm.exception.AppException;
import store.chikendev._2tm.exception.ErrorCode;
import store.chikendev._2tm.repository.ProductCommissionRepository;
import store.chikendev._2tm.repository.ProductRepository;

@Service
public class ProductCommissionService {
    @Autowired
    private ProductCommissionRepository productCommissionRepository;

    @Autowired
    private ProductRepository productRepository;
    
    public ProductCommission addProductCommission(Double commissionRate) {
        if (commissionRate >= 10) {
            throw new AppException(ErrorCode.INVALID_COMMISSION_RATE);
        }

        ProductCommission productCommission = new ProductCommission();
        productCommission.setCommissionRate(commissionRate);
        productCommission = productCommissionRepository.save(productCommission);

        List<Product> products = productRepository.findAll();
        for (Product product : products) {
            product.setProductCommission(productCommission);
            productRepository.updateProductCommission(productCommission, product.getId());
        }

        return productCommission;
    }
}
