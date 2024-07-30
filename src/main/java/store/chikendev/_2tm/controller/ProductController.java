package store.chikendev._2tm.controller;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import jakarta.validation.Valid;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import store.chikendev._2tm.dto.request.ConsignmentOrdersRequest;
import store.chikendev._2tm.dto.request.CreateProductRequest;
import store.chikendev._2tm.dto.request.ProductRequest;
import store.chikendev._2tm.dto.responce.ApiResponse;
import store.chikendev._2tm.dto.responce.ConsignmentOrdersResponse;
import store.chikendev._2tm.dto.responce.ProductResponse;
import store.chikendev._2tm.service.ProductService;
import store.chikendev._2tm.utils.EntityFileType;
import store.chikendev._2tm.utils.FilesHelp;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @PostMapping(value = "addImage", consumes = "multipart/form-data")
    public ApiResponse<String> updateImage(
        @RequestPart("id") Long id,
        @RequestPart("image") MultipartFile image
    ) {
        FilesHelp.saveFile(image, id, EntityFileType.PRODUCT);
        return new ApiResponse<>(200, null, "Update image success");
    }

    @PreAuthorize("hasAnyRole('ROLE_QLCH', 'ROLE_NVCH')")
    @PostMapping(value = "staff-create", consumes = "multipart/form-data")
    public ApiResponse<ProductResponse> staffCreate(
        @RequestPart("product") @Valid CreateProductRequest request,
        @RequestPart("images") MultipartFile[] images
    ) {
        return new ApiResponse<ProductResponse>(
            200,
            null,
            productService.staffCreateProduct(request, images)
        );
    }

    @PreAuthorize("hasAnyRole('ROLE_QLCH', 'ROLE_NVCH')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ApiResponse<Page<ProductResponse>> getAllProducts(
        @RequestParam(required = false, name = "size") Optional<Integer> size,
        @RequestParam(required = false, name = "page") Optional<Integer> page,
        @RequestParam(required = false, name = "sort") Optional<String> sort
    ) {
        Pageable pageable = PageRequest.of(page.orElse(0), size.orElse(10));
        Page<ProductResponse> products = productService.getAllProducts(
            pageable
        );
        return new ApiResponse<Page<ProductResponse>>(200, null, products);
    }

    // @PreAuthorize("hasAnyRole('ROLE_QLCH', 'ROLE_NVCH')")
    @GetMapping("/store/all")
    public ApiResponse<Page<ProductResponse>> getAllProductsInStore(
        @RequestParam(required = false, name = "size") Optional<Integer> size,
        @RequestParam(required = false, name = "pageNo") Optional<Integer> page,
        @RequestParam(required = false, name = "sort") Optional<String> sort
    ) {
        Pageable pageable = PageRequest.of(page.orElse(0), size.orElse(10));
        Page<ProductResponse> products = productService.getAllProductsInStore(
            pageable
        );
        return new ApiResponse<Page<ProductResponse>>(200, null, products);
    }

    @GetMapping("/{id}")
    public ApiResponse<ProductResponse> getProductById(
        @PathVariable("id") Long id
    ) {
        ProductResponse product = productService.getById(id);
        return new ApiResponse<ProductResponse>(200, null, product);
    }

    @GetMapping("/search")
    public ApiResponse<Page<ProductResponse>> searchProducts(
        @RequestParam(required = false, name = "value") String value,
        @RequestParam(
            required = false,
            name = "pageIndex",
            defaultValue = "0"
        ) Integer pageIndex,
        @RequestParam(
            required = false,
            name = "size",
            defaultValue = "8"
        ) Integer size
    ) {
        Page<ProductResponse> products = productService.getByNameAndDescription(
            value,
            pageIndex,
            size
        );
        System.out.println(products.getSize());
        return new ApiResponse<>(200, null, products);
    }

    @GetMapping("category/{categoryId}")
    public ApiResponse<Page<ProductResponse>> getAvailableProductsByCategory(
        @PathVariable Long categoryId,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size
    ) {
        Page<ProductResponse> products =
            productService.getAvailableProductsByCategory(
                categoryId,
                page,
                size
            );
        return new ApiResponse<Page<ProductResponse>>(200, null, products);
    }

    /**
     * Chủ hàng tạo đơn hàng ký gửi
     *
     * @param request
     * @param images
     * @return
     */
    @PreAuthorize("hasRole('ROLE_CH')")
    @PostMapping(value = "owner-create", consumes = "multipart/form-data")
    public ApiResponse<ConsignmentOrdersResponse> staffCreate(
        @RequestPart(
            "consignmentOrders"
        ) @Valid ConsignmentOrdersRequest request,
        @RequestPart("images") MultipartFile[] images
    ) {
        return new ApiResponse<ConsignmentOrdersResponse>(
            200,
            null,
            productService.ownerCreateProduct(request, images)
        );
    }

    @PreAuthorize("hasAnyRole('ROLE_QLCH', 'ROLE_NVCH')")
    @PutMapping("/update/{id}")
    public ApiResponse<ProductResponse> updateProduct(
        @PathVariable Long id,
        @RequestBody ProductRequest productRequest
    ) {
        ProductResponse updatedProduct = productService.updateProduct(
            id,
            productRequest
        );
        return new ApiResponse<ProductResponse>(200, null, updatedProduct);
    }

    @GetMapping("/consignment")
    public ApiResponse<Page<ProductResponse>> getConsignmentProducts(
        @RequestParam(
            value = "stateProduct",
            required = false
        ) Long stateProductId,
        @RequestParam(value = "page", defaultValue = "0") int page,
        @RequestParam(value = "size", defaultValue = "10") int size
    ) {
        Page<ProductResponse> products =
            productService.getConsignmentProductsByStoreAndState(
                stateProductId,
                page,
                size
            );
        return new ApiResponse<Page<ProductResponse>>(200, null, products);
    }
}
