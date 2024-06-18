package store.chikendev._2tm.service;

import org.springframework.beans.factory.annotation.Autowired;
<<<<<<< HEAD
import org.springframework.stereotype.Service;

import store.chikendev._2tm.entity.Category;
import store.chikendev._2tm.repository.CategoryRepository;

import java.util.List;

=======
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import store.chikendev._2tm.entity.Category;
import store.chikendev._2tm.repository.CategoryRepository;

>>>>>>> 654f0b004619d3a989f0851807a92f73580ca2a7
@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

<<<<<<< HEAD
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }
=======
    public Page<Category> getCategory(int page, int size) {
        return categoryRepository.findAll(PageRequest.of(page, size));
    }

>>>>>>> 654f0b004619d3a989f0851807a92f73580ca2a7
}
