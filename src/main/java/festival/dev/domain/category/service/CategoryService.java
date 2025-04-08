package festival.dev.domain.category.service;

import festival.dev.domain.category.entity.Category;

import java.util.List;

public interface CategoryService {
    Category findCategoryWithEmbedding(String name);
    Category findOrCreateByName(String name, List<Double> embedding);
    List<Double> getEmbeddingFromJson(String embeddingJson);
    Category saveCategoryWithEmbedding(String name, List<Double> embedding);
    List<Double> getCategoryVectorsFromDB(String categoryName);
}
