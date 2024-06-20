package store.chikendev._2tm.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import store.chikendev._2tm.dto.request.RequestProduct;
import store.chikendev._2tm.dto.responce.ApiResponse;
import store.chikendev._2tm.dto.responce.ProductResponse;
import store.chikendev._2tm.entity.Product;
import store.chikendev._2tm.service.ProductService;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/api/products")
public class ProductController {
    @Autowired
    private ProductService productService;

    @PostMapping
    public ResponseEntity<Product> addProduct(@RequestBody RequestProduct requestProduct) {
        try {
            Product savedProduct = productService.createProduct(
                    requestProduct.getName(),
                    requestProduct.getPrice(),
                    requestProduct.getQuantity(),
                    requestProduct.getDescription(),
                    requestProduct.getAccountId(),
                    requestProduct.getStoreId());
            return ResponseEntity.ok(savedProduct);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }



    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable Long id, @RequestBody RequestProduct requestProduct) {
        try {
            Product updatedProduct = productService.updateProduct(
                    id,
                    requestProduct.getName(),
                    requestProduct.getPrice(),
                    requestProduct.getQuantity(),
                    requestProduct.getDescription(),
                    requestProduct.getAccountId(),
                    requestProduct.getStoreId());
            return ResponseEntity.ok(updatedProduct);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }



    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    public ApiResponse<ProductResponse> getByIdProduct(@PathVariable Long id) {
        return new ApiResponse<ProductResponse>(200,null,productService.getProductById(id));
    }

    @GetMapping
    public ApiResponse<Page<ProductResponse>> getAllProducts(
        @RequestParam Optional<Integer> size,
        @RequestParam Optional<Integer> page,
        @RequestParam Optional<String> sort
        ){
        Pageable pageable = PageRequest.of(page.orElse(0), size.orElse(10));
        Page<ProductResponse> products = productService.getAllProducts(pageable);
        return new ApiResponse<Page<ProductResponse>>(200,null,products);
    }
}