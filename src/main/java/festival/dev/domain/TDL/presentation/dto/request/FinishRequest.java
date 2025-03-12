package festival.dev.domain.TDL.presentation.dto.request;

import lombok.Getter;
import lombok.NonNull;

@Getter
public class FinishRequest {
    @NonNull
    String title;
    @NonNull
    Boolean completed;
    @NonNull
    String userID;
}
