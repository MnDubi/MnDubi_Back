package festival.dev.domain.gorupTDL.presentation.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class GInsertRequest {
    @NotNull
    private String endDate;
    @NotNull
    private String title;
}
