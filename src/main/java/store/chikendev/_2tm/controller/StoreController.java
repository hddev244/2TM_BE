package store.chikendev._2tm.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
<<<<<<< HEAD
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import store.chikendev._2tm.entity.Store;
import store.chikendev._2tm.service.StoreService;

import java.util.List;

@RestController
@RequestMapping("/api/stores")
=======
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;
import store.chikendev._2tm.dto.request.StoreRequest;
import store.chikendev._2tm.dto.responce.ApiResponse;
import store.chikendev._2tm.dto.responce.StoreResponse;
import store.chikendev._2tm.service.StoreService;

@RestController
@RequestMapping("/api/store")
>>>>>>> 654f0b004619d3a989f0851807a92f73580ca2a7
public class StoreController {

    @Autowired
    private StoreService storeService;

<<<<<<< HEAD
    @GetMapping
    public List<Store> getAllStores() {
        return storeService.getAllStores();
    }
=======
    @PostMapping(consumes = "multipart/form-data")
    public ApiResponse<StoreResponse> createStore(@RequestPart("storeRequest") @Valid StoreRequest request,
            @RequestPart(name="image", required = false) MultipartFile image) {
        return new ApiResponse<>(200, null, storeService.createStore(request, image));
    }

    @GetMapping
    public StoreRequest test() {
        return new StoreRequest();
    }

    @PostMapping(value = "updateImage", consumes = "multipart/form-data")
    public ApiResponse<StoreResponse> updateImage(@RequestPart("id") Long id,
            @RequestPart("image") MultipartFile image) {
        return new ApiResponse<>(200, null, storeService.updateImage(id, image));
    }

>>>>>>> 654f0b004619d3a989f0851807a92f73580ca2a7
}
