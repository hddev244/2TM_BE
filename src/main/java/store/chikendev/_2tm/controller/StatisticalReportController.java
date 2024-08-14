package store.chikendev._2tm.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import store.chikendev._2tm.dto.responce.ApiResponse;
import store.chikendev._2tm.dto.responce.StatisticalReportResponse;
import store.chikendev._2tm.service.StatisticalReportService;

@RestController
@RequestMapping("/api/statistical-report")
public class StatisticalReportController {

    @Autowired
    private StatisticalReportService statisticalReportService;

    // CH - xem báo cáo doanh thu theo ngày truyền vào
    @GetMapping("by-date")
    public ApiResponse<Page<StatisticalReportResponse>> getByDate(
            @RequestParam(name = "date") String date,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "8") int size) {
        Page<StatisticalReportResponse> statisticalReport = statisticalReportService.getStatisticalReportByDate(date,
                page, size);
        return new ApiResponse<Page<StatisticalReportResponse>>(200, null, statisticalReport);
    }

    // CH - xem báo cáo doanh thu theo tháng truyền vào
    @GetMapping("by-month")
    public ApiResponse<Page<StatisticalReportResponse>> getByMonth(
            @RequestParam(name = "month") String month,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "8") int size) {
        Page<StatisticalReportResponse> statisticalReport = statisticalReportService.getStatisticalReportByMonth(month,
                page, size);
        return new ApiResponse<Page<StatisticalReportResponse>>(200, null, statisticalReport);
    }
}
