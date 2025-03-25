package festival.dev.domain.TDL.presentation.dto.request;

import lombok.Getter;
import lombok.NonNull;

@Getter
public class DeleteRequest {
    private String userID;
    private String title;
    private String fromDate;
}
