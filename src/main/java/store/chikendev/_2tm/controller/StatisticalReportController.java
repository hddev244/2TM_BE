package store.chikendev._2tm.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import store.chikendev._2tm.dto.responce.ApiResponse;
import store.chikendev._2tm.dto.responce.OrderResponse;
import store.chikendev._2tm.dto.responce.ProductReportResponse;
import store.chikendev._2tm.dto.responce.StatisticalReportResponse;
import store.chikendev._2tm.dto.responce.StatisticalReportRevenueResponse;
import store.chikendev._2tm.entity.Report;
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
        Page<StatisticalReportResponse> statisticalReport = statisticalReportService.getStatisticalReportByDate(
                date,
                page,
                size);
        return new ApiResponse<Page<StatisticalReportResponse>>(
                200,
                null,
                statisticalReport);
    }

    // CH - xem báo cáo doanh thu theo tháng truyền vào
    @GetMapping("by-month")
    public ApiResponse<Page<StatisticalReportResponse>> getByMonth(
            @RequestParam(name = "month") String month,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "8") int size) {
        Page<StatisticalReportResponse> statisticalReport = statisticalReportService.getStatisticalReportByMonth(
                month,
                page,
                size);
        return new ApiResponse<Page<StatisticalReportResponse>>(
                200,
                null,
                statisticalReport);
    }

    // QLCH - xem danh sách order hoàn thành theo ngày truyền vào
    @GetMapping("by-date-and-type")
    public ApiResponse<Page<OrderResponse>> getByDateAndType(
            @RequestParam(name = "date") String date,
            @RequestParam(name = "type", required = false) Boolean type,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "8") int size) {
        System.out.println(date + " TC");
        Page<OrderResponse> orders = statisticalReportService.getStatisticalReportByDateAndType(
                date,
                type,
                page,
                size);
        return new ApiResponse<Page<OrderResponse>>(200, null, orders);
    }

    // QLCH - xem danh sách order hoàn thành theo tháng truyền vào
    @GetMapping("by-month-and-type")
    public ApiResponse<Page<OrderResponse>> getByMonthAndType(
            @RequestParam(name = "month") String month,
            @RequestParam(name = "type", required = false) Boolean type,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "8") int size) {
        Page<OrderResponse> orders = statisticalReportService.getStatisticalReportByMonthAndType(
                month,
                type,
                page,
                size);
        return new ApiResponse<Page<OrderResponse>>(200, null, orders);
    }

    // QLCH - xem doanh thu theo ngày truyền vào
    @GetMapping("revenue-by-date")
    public ApiResponse<StatisticalReportRevenueResponse> getRevenueByDate(
            @RequestParam(name = "date") String date) {
        StatisticalReportRevenueResponse statisticalReport = statisticalReportService.getRevenueByDate(date);
        return new ApiResponse<StatisticalReportRevenueResponse>(
                200,
                null,
                statisticalReport);
    }

    // admin - xem doanh thu theo tháng truyền vào
    @PreAuthorize("hasRole('ROLE_QTV')")
    @GetMapping("admin/revenue/month-of-year")
    public ApiResponse<StatisticalReportRevenueResponse> getRevenueByMonth(
            @RequestParam(name = "month") String month,
            @RequestParam(name = "year") String year,
            @RequestParam(name = "storeId", required = false) Long storeId) {
        StatisticalReportRevenueResponse statisticalReport = statisticalReportService.getRevenueByDate(month);
        return new ApiResponse<StatisticalReportRevenueResponse>(
                200,
                null,
                statisticalReport);
    }

    // Admin - xem doanh thu theo min day và max day
    @PreAuthorize("hasRole('ROLE_QTV')")
    @GetMapping("admin/revenue/by-date-range")
    public ApiResponse<List<Report>> getRevenueByMinAndMaxDate(
            @RequestParam(name = "minDate") String minDate,
            @RequestParam(name = "maxDate") String maxDate,
            @RequestParam(name = "storeId", required = false) Long storeId) {
        System.out.println(minDate + " " + maxDate);

        List<Report> statisticalReport = statisticalReportService.getRevenueByMinAndMaxDate(minDate, maxDate, storeId);
        return new ApiResponse<List<Report>>(
                200,
                null,
                statisticalReport);
    }
    // ADmui ----------------------------------

    @PreAuthorize("hasRole('ROLE_QTV')")
    @GetMapping("admin/revenue/by-date")
    public ApiResponse<Report> getRevenueByDate(
            @RequestParam(name = "date") String date,
            @RequestParam(name = "storeId", required = false) Long storeId) {
        Report statisticalReport = statisticalReportService.getRevenueByDate(date, storeId);
        return new ApiResponse<Report>(
                200,
                null,
                statisticalReport);
    }

    @PreAuthorize("hasRole('ROLE_QTV')")

    @GetMapping("admin/user/group-by-role")
    public ApiResponse<List<Report>> getCountUser() {
        return new ApiResponse<List<Report>>(
                200,
                null,
                statisticalReportService.countMember());
    }

    @GetMapping("admin/user/product-report-revenue")
    public ApiResponse<List<ProductReportResponse>> getProductReportRevenue(
            @RequestParam(name = "minDate") String minDate,
            @RequestParam(name = "maxDate") String maxDate,
            @RequestParam(name = "storeId", required = false) Long storeId,
            @RequestParam(name = "productType", required = false) Boolean productType
            ) {
        return new ApiResponse<List<ProductReportResponse>>(
                200,
                null,
                statisticalReportService.getProductReportRevenue(
                        minDate, 
                        maxDate,
                        storeId,
                        productType
        ));     
    }

    // Cua hang ----------------------------------

    @PreAuthorize("hasAnyRole('ROLE_NVCH', 'ROLE_QLCH')")

    @GetMapping("store/revenue/by-date-range")
    public ApiResponse<List<Report>> getStoreRevenueByMinAndMaxDate(
            @RequestParam(name = "minDate") String minDate,
            @RequestParam(name = "maxDate") String maxDate) {

        List<Report> statisticalReport = statisticalReportService.getStoreRevenueByMinAndMaxDate(minDate, maxDate);
        return new ApiResponse<List<Report>>(
                200,
                null,
                statisticalReport);
    }

    @PreAuthorize("hasAnyRole('ROLE_NVCH', 'ROLE_QLCH')")

    @GetMapping("store/revenue/by-date")
    public ApiResponse<Report> getStoreRevenueByDate(
            @RequestParam(name = "date") String date) {
        Report statisticalReport = statisticalReportService.getStoreRevenueByDate(date);
        return new ApiResponse<Report>(
                200,
                null,
                statisticalReport);
    }

}
