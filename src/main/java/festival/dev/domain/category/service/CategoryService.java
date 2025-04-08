package festival.dev.domain.category.service;

import festival.dev.domain.category.entity.Category;

public interface CategoryService {
    Category classifyAndGetCategory(String todoContent);
}
