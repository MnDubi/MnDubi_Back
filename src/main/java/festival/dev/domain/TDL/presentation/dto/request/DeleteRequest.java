package festival.dev.domain.TDL.presentation.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class DeleteRequest {
    @NotNull
    private String userID;
    @NotNull
    private String title;
    @NotNull
    private String fromDate;
}
