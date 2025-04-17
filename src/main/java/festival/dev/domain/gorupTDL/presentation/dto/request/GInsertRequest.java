package festival.dev.domain.gorupTDL.presentation.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class GInsertRequest {
    @NotNull
    private Long groupNumber;
    @NotNull
    private String category;
    @NotNull
    private String title;
}
