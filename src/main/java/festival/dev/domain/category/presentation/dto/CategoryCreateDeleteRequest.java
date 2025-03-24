package festival.dev.domain.category.presentation.dto;

import lombok.Getter;
import lombok.NonNull;

@Getter
public class CategoryCreateDeleteRequest {
    @NonNull
    private String category;
}