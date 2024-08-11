package store.chikendev._2tm.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import store.chikendev._2tm.dto.responce.ApiResponse;
import store.chikendev._2tm.service.BillOfLadingService;

@RestController
@RequestMapping("/api/delivery")
public class DeliveryController {

    @Autowired
    private BillOfLadingService billOfLadingService;

    @PreAuthorize("hasRole('ROLE_NVGH')")
    @PutMapping(value = "update-status-order", consumes = "multipart/form-data")
    public ApiResponse<String> updateStatus(@RequestPart("idBillOfLading") Long idBillOfLading,
            @RequestPart("idStateOrder") Long idStateOrder,
            @RequestPart(required = false, name = "image") MultipartFile image) {
        return new ApiResponse<String>(200, null,
                billOfLadingService.updateStatus(idBillOfLading, idStateOrder, image));
    }
}
