package festival.dev.domain.gorupTDL.presentation.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class GDeleteRequest {
//    @NotNull
//    private String userID;
    @NotNull
    private String title;
    @NotNull
    private String endDate;
}
