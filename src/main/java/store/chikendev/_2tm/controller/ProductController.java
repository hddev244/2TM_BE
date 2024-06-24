package store.chikendev._2tm.controller;

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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import store.chikendev._2tm.dto.responce.ApiResponse;
import store.chikendev._2tm.dto.responce.ProductResponse;
import store.chikendev._2tm.service.ProductService;
import store.chikendev._2tm.utils.EntityFileType;
import store.chikendev._2tm.utils.FilesHelp;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    @Autowired
    private ProductService productService;

    @PostMapping(value = "addImage", consumes = "multipart/form-data")
    public ApiResponse<String> updateImage(@RequestPart("id") Long id,
            @RequestPart("image") MultipartFile image) {
        FilesHelp.saveFile(image, id, EntityFileType.PRODUCT);
        return new ApiResponse<>(200, null, "Update image success");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ApiResponse<Page<ProductResponse>> getAllProducts(
            @RequestParam(required = false, name = "size") Optional<Integer> size,
            @RequestParam(required = false, name = "page") Optional<Integer> page,
            @RequestParam(required = false, name = "sort") Optional<String> sort) {
        Pageable pageable = PageRequest.of(page.orElse(0), size.orElse(10));
        Page<ProductResponse> products = productService.getAllProducts(pageable);
        return new ApiResponse<Page<ProductResponse>>(200, null, products);
    }

    @GetMapping("/{id}")
    public ApiResponse<ProductResponse> getProductById(@PathVariable("id") Long id) {
        ProductResponse product = productService.getById(id);
        return new ApiResponse<ProductResponse>(200, null, product);
    }

    // @GetMapping("/products")
    // public ApiResponse<List<ProductResponse>> getProducts() {
    // List<ProductResponse> products = productService.getProducts();
    // return new ApiResponse<List<ProductResponse>>(200, null, products);
    // }

    @GetMapping("search")
    public ApiResponse<Page<ProductResponse>> searchProducts(
            @RequestParam(required = false, name = "value") String value) {
        Page<ProductResponse> products = productService.getByNameAndDescription(value);
        System.out.println(products.getSize());
        return new ApiResponse<Page<ProductResponse>>(200, null, products);
    }
}