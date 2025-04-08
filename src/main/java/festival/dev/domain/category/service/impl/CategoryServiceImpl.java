package festival.dev.domain.category.service.impl;
//
import com.fasterxml.jackson.databind.ObjectMapper;
import festival.dev.domain.category.service.CategoryService;
import festival.dev.domain.category.client.CategoryClient;
import festival.dev.domain.category.presentation.dto.CategoryResponse;
import festival.dev.domain.category.entity.Category;
import festival.dev.domain.category.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryClient categoryClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Category classifyAndGetCategory(String todoContent) {
        Map<String, List<Float>> categoryVectors = loadCategoryVectors();
        CategoryResponse response = categoryClient.classifyCategory(todoContent, categoryVectors);

        return categoryRepository.findByName(response.getCategory())
                .orElseGet(() -> {
                    Category newCategory = Category.builder()
                            .name(response.getCategory())
                            .embeddingJson(toJson(response.getEmbedding()))
                            .build();
                    return categoryRepository.save(newCategory);
                });
    }

    private Map<String, List<Float>> loadCategoryVectors() {
        List<Object[]> rows = categoryRepository.findAllNameAndEmbedding();
        Map<String, List<Float>> result = new HashMap<>();
        for (Object[] row : rows) {
            String name = (String) row[0];
            String json = (String) row[1];
            result.put(name, parseJson(json));
        }
        return result;
    }

    private List<Float> parseJson(String json) {
        try {
            Float[] arr = objectMapper.readValue(json, Float[].class);
            return Arrays.asList(arr);
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    private String toJson(List<Float> embedding) {
        try {
            return objectMapper.writeValueAsString(embedding);
        } catch (Exception e) {
            return "[]";
        }
    }
}
