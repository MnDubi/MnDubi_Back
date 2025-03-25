package festival.dev.domain.category.service.impl;

import festival.dev.domain.category.entity.Category;
import festival.dev.domain.category.presentation.dto.CategoryCreateDeleteRequest;
import festival.dev.domain.category.presentation.dto.CategoryModifyRequest;
import festival.dev.domain.category.repository.CategoryRepository;
import festival.dev.domain.category.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;

//    public Category save(CategoryCreateDeleteRequest request) {
//        Category category = Category.builder()
//                .categoryName(request.getCategory())
//                .build();
//        return categoryRepository.save(category);
//    }

    public Category modify(CategoryModifyRequest request) {
        if (!exist(request.getCategoryName())){
            throw new IllegalArgumentException("존재하지 않은 category 입니다.");
        }
        Category category = categoryRepository.findByCategoryName(request.getCategoryName());
        Category changed = category.toBuilder()
                .categoryName(request.getChangeName())
                .build();
        return categoryRepository.save(changed);
    }

    //없으면 false 있으면 true
    public boolean exist(String categoryName) {
        return categoryRepository.findByCategoryName(categoryName) != null;
    }

//    public List<Category> findAll() {
//        return categoryRepository.findAll();
//    }

    public void delete(String name) {
        categoryRepository.deleteByCategoryName(name);
    }
}
