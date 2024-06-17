package store.chikendev._2tm.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import store.chikendev._2tm.entity.Category;
import store.chikendev._2tm.repository.CategoryRepository;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    public Page<Category> getCategory(int page, int size) {
        return categoryRepository.findAll(PageRequest.of(page, size));
    }

}
