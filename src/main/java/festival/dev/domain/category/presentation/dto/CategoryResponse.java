package festival.dev.domain.category.presentation.dto;

import java.util.List;

public record CategoryResponse(String category, double similarity,
        boolean isNew,List<Double> embedding) {}


