package festival.dev.domain.TDL.presentation.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class SuccessRequest {
    @NotNull
    String title;
    @NotNull
    Boolean completed;
    @NotNull
    String userID;
}
