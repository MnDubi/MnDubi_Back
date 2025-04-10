package festival.dev.domain.gorupTDL.presentation.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class GSuccessRequest {
    @NotNull
    private String title;
    @NotNull
    private Boolean completed;
    @NotNull
    private Long groupNumber;
    @NotNull
    private String senderID;
}
