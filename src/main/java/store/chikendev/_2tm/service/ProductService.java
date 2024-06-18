package store.chikendev._2tm.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import store.chikendev._2tm.entity.Account;
import store.chikendev._2tm.entity.Product;
import store.chikendev._2tm.entity.Store;
import store.chikendev._2tm.exception.AppException;
import store.chikendev._2tm.exception.ErrorCode;
import store.chikendev._2tm.repository.AccountRepository;
import store.chikendev._2tm.repository.ProductRepository;
import store.chikendev._2tm.repository.StoreRepository;

@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private StoreRepository storeRepository;

    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

    public Product createProduct(String name, Double price, Integer quantity, String description, String accountId,
            Long storeId) {
        Optional<Account> accountOpt = accountRepository.findById(accountId);
        Optional<Store> storeOpt = storeRepository.findById(storeId);

        if (accountOpt.isPresent() || storeOpt.isPresent()) {
            Product product = new Product();
            product.setName(name);
            product.setPrice(price);
            product.setQuantity(quantity);
            product.setDescription(description);
            product.setAccount(accountOpt.get());
            product.setStore(storeOpt.get());

            return productRepository.save(product);
        } else {
            throw new IllegalArgumentException("Invalid accountId or storeId");
        }
    }

    public Product updateProduct(Long id, String name, Double price, Integer quantity, String description,
            String accountId, Long storeId) {
        Optional<Account> accountOpt = accountRepository.findById(accountId);
        Optional<Store> storeOpt = storeRepository.findById(storeId);

        if (accountOpt.isPresent() && storeOpt.isPresent()) {
            Optional<Product> existingProductOpt = productRepository.findById(id);
            if (existingProductOpt.isPresent()) {
                Product existingProduct = existingProductOpt.get();
                existingProduct.setName(name);
                existingProduct.setPrice(price);
                existingProduct.setQuantity(quantity);
                existingProduct.setDescription(description);
                existingProduct.setAccount(accountOpt.get());
                existingProduct.setStore(storeOpt.get());
                return productRepository.save(existingProduct);
            } else {
                throw new AppException(ErrorCode.USER_EXISTED);// fix
            }
        } else {
            throw new IllegalArgumentException("Invalid accountId or storeId");
        }
    }
}