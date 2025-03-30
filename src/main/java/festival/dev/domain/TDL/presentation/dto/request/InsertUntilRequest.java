package festival.dev.domain.TDL.presentation.dto.request;

import lombok.Getter;

@Getter
public class InsertUntilRequest {
    private String title;
    private String category;
    private String startDate;
    private String endDate;
}
