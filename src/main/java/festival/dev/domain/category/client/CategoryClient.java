package festival.dev.domain.category.client;

import festival.dev.domain.category.presentation.dto.CategoryResponse;
import festival.dev.domain.category.presentation.dto.CategoryRequest;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.*;

@Component
@RequiredArgsConstructor
public class CategoryClient {

    private final WebClient webClient = WebClient.builder()
            .baseUrl("http://localhost:5001")  // FastAPI 서버 주소
            .build();

    public CategoryResponse classifyCategory(String todo, Map<String, List<Double>> categoryVectors) {
        CategoryRequest request = new CategoryRequest(todo, categoryVectors);
        return webClient.post()
                .uri("/classify-category")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(CategoryResponse.class)
                .block();
    }
}