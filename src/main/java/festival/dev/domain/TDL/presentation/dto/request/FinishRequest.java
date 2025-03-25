package festival.dev.domain.TDL.presentation.dto.request;

import lombok.Getter;
import lombok.NonNull;

@Getter
public class FinishRequest {
    private int part;
    private int every;
    private String userID;
}
