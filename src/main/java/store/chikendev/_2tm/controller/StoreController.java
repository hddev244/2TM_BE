package store.chikendev._2tm.controller;

import jakarta.validation.Valid;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import store.chikendev._2tm.dto.request.StoreRequest;
import store.chikendev._2tm.dto.responce.ApiResponse;
import store.chikendev._2tm.dto.responce.StoreResponse;
import store.chikendev._2tm.service.StoreService;

@RestController
@RequestMapping("/api")
public class StoreController {

    @Autowired
    private StoreService storeService;

    @PreAuthorize("hasAnyRole('ROLE_QTV')")
    @PostMapping(value = "/store", consumes = "multipart/form-data")
    public ApiResponse<StoreResponse> createStore(
        @RequestPart("storeRequest") @Valid StoreRequest request,
        @RequestPart(name = "image", required = false) MultipartFile image
    ) {
        return new ApiResponse<>(
            200,
            null,
            storeService.createStore(request, image)
        );
    }

    @PreAuthorize("hasAnyRole('ROLE_QTV', 'ROLE_QLCH')")
    @PostMapping(value = "store/updateImage", consumes = "multipart/form-data")
    public ApiResponse<StoreResponse> updateImage(
        @RequestPart("id") Long id,
        @RequestPart("image") MultipartFile image
    ) {
        return new ApiResponse<>(
            200,
            null,
            storeService.updateImage(id, image)
        );
    }

    /**
     * Get store by district id
     *
     * @param districtId
     * @return StoreResponse
     */
    @GetMapping("store/district")
    public ApiResponse<List<StoreResponse>> getStoreByDistrictId(
        @RequestParam("id") Long districtId
    ) {
        List<StoreResponse> storeResponse = storeService.getStoreByDistrictId(
            districtId
        );
        return new ApiResponse<>(200, null, storeResponse);
    }

    @GetMapping("store")
    public ApiResponse<List<StoreResponse>> getAllStore() {
        List<StoreResponse> responses = storeService.getAllStores();
        return new ApiResponse<>(200, null, responses);
    }

    @GetMapping("admin/store")
    public ApiResponse<List<StoreResponse>> getAdminAllStore() {
        List<StoreResponse> responses = storeService.getAllStoresAdmin();
        return new ApiResponse<>(200, null, responses);
    }

    // @PreAuthorize("hasAnyRole('ROLE_QTV')")
    @GetMapping("admin/store/{id}")
    public ApiResponse<StoreResponse> getAdminStoreById(
        @PathVariable("id") Long id
    ) {
        StoreResponse responses = storeService.getStoreById(id);
        return new ApiResponse<StoreResponse>(200, null, responses);
    }

    // @PreAuthorize("hasAnyRole('ROLE_QTV')")
    @PutMapping("admin/store/{id}")
    public ApiResponse<StoreResponse> updateStore(
        @PathVariable("id") Long id,
        @RequestBody StoreRequest request
    ) {
        StoreResponse responses = storeService.updateStore(id, request);
        return new ApiResponse<StoreResponse>(200, null, responses);
    }

    @PreAuthorize("hasAnyRole('ROLE_QTV')")
    @PutMapping("store/update-state/{id}")
    public ApiResponse<StoreResponse> updateState(@PathVariable("id") Long id) {
        StoreResponse responses = storeService.updateState(id);
        return new ApiResponse<StoreResponse>(200, null, responses);
    }
}
