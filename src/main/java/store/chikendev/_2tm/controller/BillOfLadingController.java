package store.chikendev._2tm.controller;

import org.springframework.web.bind.annotation.RestController;

@RestController
public class BillOfLadingController {

    // @Autowired
    // private BillOfLadingService billOfLadingService;

    // @PostMapping("/addBillOfLading")
    // public ApiResponse<BillOfLadingResponse> addBillOfLading(@RequestBody
    // BillOfLadingRequest request) {
    // BillOfLadingResponse response = billOfLadingService.addBillOfLading(request);
    // System.out.println(response);
    // return new ApiResponse<>(200, null, response);
    // }

    // @GetMapping("/shipList/{deliveryPerson}")
    // public ApiResponse<List<BillOfLadingResponse>>
    // getShipList(@PathVariable("deliveryPerson") String deliveryPerson) {
    // return new ApiResponse<List<BillOfLadingResponse>>(200, null,
    // billOfLadingService.getBillOfLadingByDeliveryPersonId(deliveryPerson));
    // }

    // @GetMapping("/billOfLadingList")
    // public ApiResponse<List<BillOfLadingResponse>> getAll() {
    // return new ApiResponse<List<BillOfLadingResponse>(200, null,
    // billOfLadingService.getAllBillOfLadings());
    // }

}
