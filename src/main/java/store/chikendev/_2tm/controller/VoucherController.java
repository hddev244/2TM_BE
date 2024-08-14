package store.chikendev._2tm.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import store.chikendev._2tm.dto.request.VoucherRequest;
import store.chikendev._2tm.dto.responce.ApiResponse;
import store.chikendev._2tm.dto.responce.VoucherResponse;
import store.chikendev._2tm.service.VoucherService;

@RestController
@RequestMapping("/api/voucher")
public class VoucherController {
    @Autowired
    private VoucherService voucherService;

    @PostMapping("/add")
    public ApiResponse<VoucherResponse> addVoucher(@RequestBody VoucherRequest request) {
        VoucherResponse response = voucherService.addVoucher(request);
        return new ApiResponse<VoucherResponse>(200, null, response);
    }

    @PostMapping("/update/{id}")
    public ApiResponse<VoucherResponse> addVoucher(@PathVariable("id") String id, @RequestBody VoucherRequest request) {
        VoucherResponse response = voucherService.updateVoucher(request);
        return new ApiResponse<VoucherResponse>(200, null, response);
    }

    @GetMapping
    public ApiResponse<List<VoucherResponse>> getAll() {
        return new ApiResponse<List<VoucherResponse>>(200, null, voucherService.getAll());
    }

    @GetMapping("/{id}")
    public ApiResponse<VoucherResponse> getById(@PathVariable("id") String id) {
        VoucherResponse response = voucherService.getById(id);
        return new ApiResponse<VoucherResponse>(200, null, response);
    }
}
