package festival.dev.domain.gorupTDL.presentation.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class GInsertRequest {
    @NotNull
    private String title;
//    @NotNull
//    private String userID;
    @NotNull
    private String category;
    @NotNull
    private String endDate;
}
