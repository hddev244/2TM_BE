package store.chikendev._2tm.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import store.chikendev._2tm.dto.responce.ApiResponse;
import store.chikendev._2tm.dto.responce.DistrictResponse;
import store.chikendev._2tm.dto.responce.ProvinceCityResponse;
import store.chikendev._2tm.dto.responce.WardResponse;
import store.chikendev._2tm.service.AddressService;

import org.springframework.web.bind.annotation.RequestMapping;

import store.chikendev._2tm.dto.request.AddressRequest;
import store.chikendev._2tm.dto.responce.AddressResponse;

@RestController
@RequestMapping("/api/address")
public class AddressController {
    @Autowired
    private AddressService addressService;

    @GetMapping("/provinceCity/{id}")
    public ApiResponse<ProvinceCityResponse> getProvinceCity(@PathVariable("id") Long id) {
        return new ApiResponse<ProvinceCityResponse>(200, null, addressService.getByIdProvince(id));
    }

    @GetMapping("/district/{province_id}")
    public ApiResponse<List<DistrictResponse>> getByIdProvince(@PathVariable("province_id") Long province_id) {
        List<DistrictResponse> district = addressService.findDistrictByProvinceId(province_id);
        return new ApiResponse<List<DistrictResponse>>(200, null, district);
    }

    @GetMapping("/ward/{district_id}")
    public ApiResponse<List<WardResponse>> findByDistrictId(@PathVariable("district_id") Long district_id) {
        return new ApiResponse<List<WardResponse>>(200, null, addressService.findByDistrictId(district_id));
    }

    @PostMapping("/addAddress")
    public ApiResponse<AddressResponse> address(@RequestBody AddressRequest request) {
        return new ApiResponse<>(200, null, addressService.addAddress(request));
    }

}
