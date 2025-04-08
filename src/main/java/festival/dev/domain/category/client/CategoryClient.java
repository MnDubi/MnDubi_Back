package festival.dev.domain.category.client;

import festival.dev.domain.category.presentation.dto.CategoryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.*;

@Component
@RequiredArgsConstructor
public class CategoryClient {

    private final WebClient webClient = WebClient.create("http://localhost:5001");

    public CategoryResponse classifyCategory(String todo, Map<String, List<Float>> categoryVectors) {
        return webClient.post()
                .uri("/classify-category")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(Map.of("todo", todo, "category_vectors", categoryVectors))
                .retrieve()
                .bodyToMono(CategoryResponse.class)
                .block();
    }
}