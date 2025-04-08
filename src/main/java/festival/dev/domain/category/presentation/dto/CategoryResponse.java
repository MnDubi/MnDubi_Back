package festival.dev.domain.category.presentation.dto;


import lombok.Data;
import java.util.List;

@Data
public class CategoryResponse {
    private String category;
    private double similarity;
    private boolean isNew;
    private List<Float> embedding;
}