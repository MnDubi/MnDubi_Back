package festival.dev.domain.gorupTDL.presentation.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GToDoListResponse {
    private String endDate;
    private String startDate;
    private String userID;
    private String title;
    private boolean completed;
    private String category;
}
