package store.chikendev._2tm.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import store.chikendev._2tm.dto.responce.ApiResponse;
import store.chikendev._2tm.dto.responce.CategoryResponse;
import store.chikendev._2tm.dto.responce.ProductResponse;
import store.chikendev._2tm.service.CategoryService;
import store.chikendev._2tm.service.ProductService;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ProductService productService;

    @GetMapping
    public ApiResponse<List<CategoryResponse>> getAllCategories() {
        List<CategoryResponse> categoryResponses =
            categoryService.getAllCategories();

        return new ApiResponse<List<CategoryResponse>>(
            200,
            null,
            categoryResponses
        );
    }

    @GetMapping("/{path}/products")
    public ApiResponse<Page<ProductResponse>> getCategoryById(
        @PathVariable("path") String path,
        @RequestParam(
            name = "pageNo",
            required = false,
            defaultValue = "0"
        ) Integer pageNo,
        @RequestParam(
            name = "pageSize",
            required = false,
            defaultValue = "10"
        ) Integer pageSize
    ) {
        Page<ProductResponse> products =
            productService.getProductsByCategoryPath(path, pageNo, pageSize);

        return new ApiResponse<Page<ProductResponse>>(200, null, products);
    }
}
