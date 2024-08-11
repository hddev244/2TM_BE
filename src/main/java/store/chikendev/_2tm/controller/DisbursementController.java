package store.chikendev._2tm.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import store.chikendev._2tm.dto.responce.ApiResponse;
import store.chikendev._2tm.dto.responce.DisbursementsResponse;
import store.chikendev._2tm.service.DisbursementsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api/disbursement")
public class DisbursementController {

    @Autowired
    private DisbursementsService disbursementService;

    @GetMapping("/state")
    public ApiResponse<List<DisbursementsResponse>> getDisbursements(
            @RequestParam(required = false, name = "state") Boolean state) {
        return new ApiResponse<List<DisbursementsResponse>>(200, null,
                disbursementService.findbyDisbursementsByAccountAndState(state));
    }
}
