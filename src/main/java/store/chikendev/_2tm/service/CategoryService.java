package store.chikendev._2tm.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import store.chikendev._2tm.dto.responce.CategoryResponse;
import store.chikendev._2tm.dto.responce.ResponseDocumentDto;
import store.chikendev._2tm.entity.Category;
import store.chikendev._2tm.repository.CategoryRepository;
import store.chikendev._2tm.utils.EntityFileType;
import store.chikendev._2tm.utils.FilesHelp;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    public List<CategoryResponse> getAllCategories() {
        List<Category> categories = categoryRepository.findAll();
        List<CategoryResponse> categoryResponses = categories
            .stream()
            .map(this::convertToResponse)
            .collect(Collectors.toList());
        return categoryResponses;
    }

    private CategoryResponse convertToResponse(Category category) {
        CategoryResponse categoryResponse = new CategoryResponse();
        categoryResponse.setId(category.getId());
        categoryResponse.setName(category.getName());
        ResponseDocumentDto responseDocument = FilesHelp.getOneDocument(
            categoryResponse.getId(),
            EntityFileType.CATEGORY
        );
        categoryResponse.setImage(responseDocument);
        categoryResponse.setPath(category.getPath());
        return categoryResponse;
    }
}
