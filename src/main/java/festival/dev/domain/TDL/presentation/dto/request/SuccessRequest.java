package festival.dev.domain.TDL.presentation.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class SuccessRequest {
    @NotNull
    private String title;
    @NotNull
    private Boolean completed;
    @NotNull
    private String userID;
}
