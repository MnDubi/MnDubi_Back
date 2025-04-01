package festival.dev.domain.TDL.presentation.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class InsertRequest {
    @NotNull
    private String title;
//    @NotNull
//    private String userID;
    @NotNull
    private String category;
    @NotNull
    private String endDate;
}
