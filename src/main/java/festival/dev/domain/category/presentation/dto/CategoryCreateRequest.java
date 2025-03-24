package festival.dev.domain.category.presentation.dto;

import lombok.Getter;
import lombok.NonNull;

@Getter
public class CategoryCreateRequest {
    @NonNull
    private String category;
}