package festival.dev.domain.TDL.presentation.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class FinishRequest {
    @NotNull
    private int part;
    @NotNull
    private int every;
//    @NotNull
//    private String userID;
}
