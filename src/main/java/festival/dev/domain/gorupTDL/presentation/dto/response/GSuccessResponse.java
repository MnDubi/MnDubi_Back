package festival.dev.domain.gorupTDL.presentation.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GSuccessResponse {
    private String endDate;
    private String startDate;
    private String ownerID;
    private String title;
    private boolean completed;
    private Long groupNumber;
    private String category;
    private String receiverID;
}
