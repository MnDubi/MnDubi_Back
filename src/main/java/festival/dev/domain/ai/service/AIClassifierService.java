package festival.dev.domain.ai.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Value;
import festival.dev.domain.ai.service.dto.AIClassifyRequest;
import festival.dev.domain.ai.service.dto.AIClassifyResponse;

import java.util.List;
import java.util.Map;


@Service
@RequiredArgsConstructor
public class AIClassifierService {

    private final RestTemplate restTemplate;

    @Value("${ai.server.url}")
    private String aiServerUrl;  // ex) http://localhost:5001

    public String classifyCategoryWithAI(String todo, Map<String, List<Double>> categoryVectors) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // 요청 body 구성
        Map<String, Object> requestBody = Map.of(
                "todo", todo,
                "category_vectors", categoryVectors
        );

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(
                    aiServerUrl + "/classify-category",
                    HttpMethod.POST,
                    entity,
                    Map.class
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                return (String) response.getBody().getOrDefault("category", "기타");
            }
        } catch (Exception e) {
            e.printStackTrace();  // 필요 시 log.warn(...) 처리 가능
        }

        return "기타";  // fallback
    }
}
