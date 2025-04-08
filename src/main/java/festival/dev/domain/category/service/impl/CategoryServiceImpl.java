package festival.dev.domain.category.service.impl;

import festival.dev.domain.category.entity.Category;
import festival.dev.domain.category.repository.CategoryRepository;
import festival.dev.domain.category.service.CategoryService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final ObjectMapper objectMapper;  // Jackson ObjectMapper for JSON parsing

    // 카테고리 이름으로 카테고리 찾기
    @Override
    public Category findCategoryWithEmbedding(String name) {
        return categoryRepository.findByName(name)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 카테고리입니다."));
    }

    // 카테고리가 없으면 AI 서버로 받아와서 카테고리 생성, 임베딩 저장
    @Override
    @Transactional
    public Category findOrCreateByName(String name, List<Double> embedding) {
        // DB에서 카테고리 찾기
        Optional<Category> existingCategory = categoryRepository.findByName(name);

        if (existingCategory.isPresent()) {
            return existingCategory.get();  // 이미 존재하는 카테고리 반환
        } else {
            // 카테고리 생성
            Category newCategory = new Category(name, convertEmbeddingToJson(embedding));
            return categoryRepository.save(newCategory);  // 새 카테고리 DB에 저장
        }
    }

    // 카테고리 벡터를 JSON 형태로 변환해서 저장
    private String convertEmbeddingToJson(List<Double> embedding) {
        try {
            return objectMapper.writeValueAsString(embedding);  // List<Double> -> JSON String 변환
        } catch (IOException e) {
            throw new RuntimeException("임베딩 벡터를 JSON으로 변환할 수 없습니다.", e);
        }
    }

    // JSON 형태의 임베딩을 List<Double>으로 변환
    @Override
    public List<Double> getEmbeddingFromJson(String embeddingJson) {
        try {
            return objectMapper.readValue(embeddingJson, new TypeReference<List<Double>>() {});
        } catch (IOException e) {
            throw new RuntimeException("임베딩 데이터를 변환할 수 없습니다.", e);
        }
    }

    public List<Double> getCategoryVectorsFromDB(String name) {
        // 카테고리 이름으로 카테고리 객체 찾기
        Category category = categoryRepository.findByName(name)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 카테고리입니다."));

        // 카테고리의 임베딩 벡터를 JSON에서 List<Double>으로 변환
        return getEmbeddingFromJson(category.getEmbeddingJson());
    }

    // 새 카테고리 저장
    @Override
    @Transactional
    public Category saveCategoryWithEmbedding(String name, List<Double> embedding) {
        Category category = new Category(name, convertEmbeddingToJson(embedding));
        return categoryRepository.save(category);
    }


}
