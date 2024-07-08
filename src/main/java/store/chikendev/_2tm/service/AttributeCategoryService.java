package store.chikendev._2tm.service;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import store.chikendev._2tm.dto.responce.AttributeDetailResponse;
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

    @Autowired
    private ModelMapper mapper;

    public List<ProductAttributeResponse> getByCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));
        List<AttributeCategory> attributeCategories = attributeCategoryRepositosy.findByCategory(category);

        System.out.println("attributeCategories: " + category.getAttributes().get(0).getAttributeDetails().size());
        
        return attributeCategories.stream().map(attributeCategory -> ProductAttributeResponse.builder()
                .id(attributeCategory.getAttribute().getId())
                .name(attributeCategory.getAttribute().getName())
                .detail(attributeCategory.getAttributeDetails().stream()
                .map(detail -> mapper.map(detail, AttributeDetailResponse.class)).toList())
                .build()).collect(Collectors.toList());
    }
}
