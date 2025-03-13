package festival.dev.domain.TDL.presentation.dto.request;

import lombok.Getter;
import lombok.NonNull;

@Getter
public class FinishRequest {
    @NonNull
    private int part;
    @NonNull
    private int every;
    @NonNull
    private String userID;
}
