package festival.dev.domain.TDL.presentation.dto.request;

import lombok.Getter;

@Getter
public class InsertRequest {
    private String title;
    private String userID;
    private String category;
    private String fromDate;
}
