package store.chikendev._2tm.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;
import store.chikendev._2tm.dto.request.ConsignmentOrdersRequest;
import store.chikendev._2tm.dto.request.CreateProductRequest;
import store.chikendev._2tm.dto.responce.ProductResponse;
import store.chikendev._2tm.service.ConsignmentOrdersService;
import store.chikendev._2tm.service.ProductService;
import store.chikendev._2tm.dto.responce.ApiResponse;

@RestController
@RequestMapping("/api/consignment")
public class ConsignmentOrdersController {

    @Autowired
    private ConsignmentOrdersService consignmentOrdersService;

    @Autowired
    private ProductService productService;

    // @PostMapping(value = "create-product", consumes = "multipart/form-data")
    // public ApiResponse<ProductResponse> staffCreate(@RequestPart("product")
    // @Valid CreateProductRequest request,
    // @RequestPart("images") MultipartFile[] images) {
    // return new ApiResponse<ProductResponse>(200, null,
    // productService.CustomerCreateProduct(request, images));
    // }

    // @PostMapping(value = "create-order")
    // public ApiResponse<String> createOrder(@RequestBody @Valid
    // ConsignmentOrdersRequest request) {
    // return new ApiResponse<String>(200, null,
    // consignmentOrdersService.createConsignmentOrders(request));
    // }
}
