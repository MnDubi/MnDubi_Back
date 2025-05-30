package festival.dev.domain.TDL.presentation.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class ShareRequest {
    @NotNull
    private String title;
    @NotNull
    private Boolean shared;
    @NotNull
    private String endDate;
}
