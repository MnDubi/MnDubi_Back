package festival.dev.domain.TDL.presentation.dto.request;

import lombok.Getter;

@Getter
public class FinishRequest {
    private int part;
    private int every;
    private String userID;
}
