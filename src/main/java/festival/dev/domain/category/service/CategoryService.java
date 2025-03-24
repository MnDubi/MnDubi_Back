package festival.dev.domain.category.service;

import festival.dev.domain.category.entity.Category;
import festival.dev.domain.category.presentation.dto.CategoryCreateRequest;
import festival.dev.domain.category.presentation.dto.CategoryModifyRequest;

import java.util.List;

public interface CategoryService {
    Category save(CategoryCreateRequest request);
    Category modify(CategoryModifyRequest request);
    List<Category> findAll();
}
