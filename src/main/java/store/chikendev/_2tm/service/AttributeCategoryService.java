package store.chikendev._2tm.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import store.chikendev._2tm.dto.responce.ProductAttributeResponse;
import store.chikendev._2tm.entity.AttributeCategory;
import store.chikendev._2tm.entity.Category;
import store.chikendev._2tm.exception.AppException;
import store.chikendev._2tm.exception.ErrorCode;
import store.chikendev._2tm.repository.AttributeCategoryRepository;
import store.chikendev._2tm.repository.CategoryRepository;

@Service
public class AttributeCategoryService {

    @Autowired
    private AttributeCategoryRepository attributeCategoryRepositosy;

    @Autowired
    private CategoryRepository categoryRepository;

    public List<ProductAttributeResponse> getByCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));
        List<AttributeCategory> attributeCategories = attributeCategoryRepositosy.findByCategory(category);
        return attributeCategories.stream().map(attributeCategory -> ProductAttributeResponse.builder()
                .id(attributeCategory.getAttribute().getId())
                .name(attributeCategory.getAttribute().getName())
                .build()).collect(Collectors.toList());
    }
}
