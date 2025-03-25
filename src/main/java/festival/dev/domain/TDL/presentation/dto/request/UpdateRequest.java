package festival.dev.domain.TDL.presentation.dto.request;

import jakarta.validation.constraints.AssertTrue;
import lombok.Getter;
import lombok.NonNull;

@Getter
public class UpdateRequest {
    @NonNull
    private String userID;
    @NonNull
    private String title;
    @NonNull
    private String change;
    @NonNull
    private String fromDate;
    @NonNull
    private String changeDate;

    @AssertTrue(message = "예전 제목과 바뀐 제목, 또는 예전 날짜와 바뀐 날짜 중 하나는 달라야 합니다.")
    public boolean isValidUpdate() {
        return !title.equals(change) || !fromDate.equals(changeDate);
    }
}
