package store.chikendev._2tm.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import store.chikendev._2tm.dto.responce.ApiResponse;
import store.chikendev._2tm.dto.responce.ProductAttributeResponse;
import store.chikendev._2tm.service.AttributeCategoryService;

@RestController
@RequestMapping("/api")
public class AttributeCategoryController {

    @Autowired
    private AttributeCategoryService attributeCategoryService;

    @PostMapping("attributeByCategory")
    public ApiResponse<List<ProductAttributeResponse>> getAttributeByCategory(@RequestParam Long id) {
        List<ProductAttributeResponse> response = attributeCategoryService.getByCategory(id);
        return new ApiResponse<List<ProductAttributeResponse>>(200, null, response);
    }

}
