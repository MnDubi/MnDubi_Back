package festival.dev.domain.shareTDL.presentation.dto.request;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class ShareModifyReq {
    @NotNull
    private String title;
    @NotNull
    private String change;

    @AssertTrue(message = "예전 제목과 바뀐 제목, 또는 예전 날짜와 바뀐 날짜 중 하나는 달라야 합니다.")
    public boolean isValidUpdate() {
        return !title.equals(change);
    }
}
