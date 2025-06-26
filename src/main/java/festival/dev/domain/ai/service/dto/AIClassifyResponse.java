package festival.dev.domain.ai.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AIClassifyResponse {
    private String category;
    private double similarity;
    private boolean isNew;
    private List<Double> embedding;
}