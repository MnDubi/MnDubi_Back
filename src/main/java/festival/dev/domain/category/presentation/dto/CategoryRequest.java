package festival.dev.domain.category.presentation.dto;

import java.util.*;
public record CategoryRequest(String todo, Map<String, List<Double>> category_vectors) {}
