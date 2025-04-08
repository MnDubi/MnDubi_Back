package festival.dev.domain.ai.service;

import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.core.ParameterizedTypeReference;

import java.util.List;
import java.util.Map;

@Service
public class AIClassifierService {

    private final RestTemplate restTemplate;

    public AIClassifierService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    // AI 서버에 투두 내용(title) 보내서 카테고리 자동 분류
    public String classifyCategoryWithAI(String title, List<Double> categoryVectors) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // AI 서버에 보내는 데이터
        Map<String, Object> requestBody = Map.of(
                "todo", title,
                "category_vectors", categoryVectors
        );

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        // FastAPI 서버에 POST 요청 보내기
        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                "http://localhost:5001/classify-category",  // FastAPI URL
                HttpMethod.POST,
                entity,
                new ParameterizedTypeReference<Map<String, Object>>() {}
        );

        // AI 서버에서 받은 카테고리 이름 반환
        return (String) response.getBody().get("category");
    }
}
