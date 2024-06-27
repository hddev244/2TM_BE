package store.chikendev._2tm.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
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
import store.chikendev._2tm.service.StoreService;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/api/store")
public class StoreController {

    @Autowired
    private StoreService storeService;

    @PreAuthorize("hasAnyRole('ROLE_QTV')")
    @PostMapping(consumes = "multipart/form-data")
    public ApiResponse<StoreResponse> createStore(@RequestPart("storeRequest") @Valid StoreRequest request,
            @RequestPart(name = "image", required = false) MultipartFile image) {
        return new ApiResponse<>(200, null, storeService.createStore(request, image));
    }
   
    @PreAuthorize("hasAnyRole('ROLE_QTV', 'ROLE_QLCH')")
    @PostMapping(value = "updateImage", consumes = "multipart/form-data")
    public ApiResponse<StoreResponse> updateImage(@RequestPart("id") Long id,
            @RequestPart("image") MultipartFile image) {
        return new ApiResponse<>(200, null, storeService.updateImage(id, image));
    }

    /**
     * Get store by district id
     * @param districtId
     * @return StoreResponse
     */
    @GetMapping("district")
    public ApiResponse<List<StoreResponse>> getStoreByDistrictId(@RequestParam("id") Long districtId) {
        List<StoreResponse> storeResponse = storeService.getStoreByDistrictId(districtId);
        return new ApiResponse<>(200, null, storeResponse);
    }
    

    @GetMapping
    public ApiResponse<List<StoreResponse>> getAllStore() {
        List<StoreResponse> responses = storeService.getAllStores();
        return new ApiResponse<>(200, null, responses);
    }

}
