package store.chikendev._2tm.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
    public ApiResponse<Page<DisbursementsResponse>> getDisbursements(
            @RequestParam(required = false, name = "state") Boolean state,
            @RequestParam(defaultValue = "0", name = "page") int page,
            @RequestParam(defaultValue = "10", name = "size") int size) {
        PageRequest pageable = PageRequest.of(page, size);
        return new ApiResponse<Page<DisbursementsResponse>>(200, null,
                disbursementService.findbyDisbursementsByAccountAndState(state, pageable));
    }
}
