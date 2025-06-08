package festival.dev.domain.ai.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;
import java.util.Map;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class AIClassifyRequest {
    private String todo;
    private Map<String, List<Double>> category_vectors;
}
