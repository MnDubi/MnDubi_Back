package festival.dev.domain.gorupTDL.presentation.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class GChoiceRequest {
    @NotNull
    private Long groupNumber;
}
