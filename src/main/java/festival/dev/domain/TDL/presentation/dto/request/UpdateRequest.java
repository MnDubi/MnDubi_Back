package festival.dev.domain.TDL.presentation.dto.request;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class UpdateRequest {
//    @NotNull
//    private String userID;
    @NotNull
    private String title;
    @NotNull
    private String change;
    @NotNull
    private String fromDate;
    @NotNull
    private String changeDate;

    @AssertTrue(message = "예전 제목과 바뀐 제목, 또는 예전 날짜와 바뀐 날짜 중 하나는 달라야 합니다.")
    public boolean isValidUpdate() {
        return !title.equals(change) || !fromDate.equals(changeDate);
    }
}
