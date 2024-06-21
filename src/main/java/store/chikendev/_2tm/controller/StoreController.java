package store.chikendev._2tm.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;
import store.chikendev._2tm.dto.request.StoreRequest;
import store.chikendev._2tm.dto.responce.ApiResponse;
import store.chikendev._2tm.dto.responce.StoreResponse;
import store.chikendev._2tm.entity.Store;
import store.chikendev._2tm.service.StoreService;

@RestController
@RequestMapping("/api/store")
public class StoreController {

    @Autowired
    private StoreService storeService;

    @PostMapping(consumes = "multipart/form-data")
    public ApiResponse<StoreResponse> createStore(@RequestPart("storeRequest") @Valid StoreRequest request,
            @RequestPart(name="image", required = false) MultipartFile image) {
        return new ApiResponse<>(200, null, storeService.createStore(request, image));
    }


    @PostMapping(value = "updateImage", consumes = "multipart/form-data")
    public ApiResponse<StoreResponse> updateImage(@RequestPart("id") Long id,
            @RequestPart("image") MultipartFile image) {
        return new ApiResponse<>(200, null, storeService.updateImage(id, image));
    }

     @GetMapping
    public ResponseEntity<List<StoreResponse>> getAllStores() {
        List<Store> stores = storeService.getAllStores();
        List<StoreResponse> storeResponses = stores.stream().map(this::convertToResponse).collect(Collectors.toList());
        return ResponseEntity.ok(storeResponses);
    }

    private StoreResponse convertToResponse(Store store) {
        StoreResponse storeResponse = new StoreResponse();
        storeResponse.setId(store.getId());
        storeResponse.setName(store.getName());
        storeResponse.setPostalCode(store.getPostalCode());
        storeResponse.setPhone(store.getPhone());
        storeResponse.setEmail(store.getEmail());
        storeResponse.setStreetAddress(store.getStreetAddress());
        storeResponse.setDescription(store.getDescription());
        return storeResponse;
    }

}
