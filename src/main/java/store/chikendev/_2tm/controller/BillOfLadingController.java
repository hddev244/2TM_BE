package store.chikendev._2tm.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import store.chikendev._2tm.service.BillOfLadingService;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


import jakarta.validation.Valid;
import store.chikendev._2tm.dto.request.BillOfLadingRequest;
import store.chikendev._2tm.dto.responce.ApiResponse;
import store.chikendev._2tm.dto.responce.BillOfLadingResponse;


@RestController
public class BillOfLadingController {
    
    @Autowired
    private BillOfLadingService billOfLadingService;

    // @PostMapping("/addBillOfLading")
    // public ApiResponse<BillOfLadingResponse> addBillOfLading(@RequestBody @Valid BillOfLadingRequest request){
    //             // @RequestPart(name="image", required = false) MultipartFile image)
    //     return new ApiResponse<BillOfLadingResponse>(200,null, billOfLadingService.addBillOfLading(request));
    // }
    // @PostMapping(value= "/addBillOfLading",consumes = "multipart/form-data")
    // public ApiResponse<String> addBillOfLading(
    //     @RequestPart("addBillOfLading")  String request
    //     // , @RequestPart(name="image", required = false) MultipartFile image
    //             ) {
    //                 System.out.println(request);
    //     return new ApiResponse<>(200,null,"addBillOfLading");
    // }


    @PostMapping("/addBillOfLading")
    public ApiResponse<BillOfLadingResponse> addBillOfLading(@RequestBody BillOfLadingRequest request) {
        BillOfLadingResponse response = billOfLadingService.addBillOfLading(request);
        System.out.println(response);
        return new  ApiResponse<>(200, null, response);
    }
    
    
}
