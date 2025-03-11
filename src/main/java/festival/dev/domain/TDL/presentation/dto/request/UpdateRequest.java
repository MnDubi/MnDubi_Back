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

    @AssertTrue(message = "title과 change는 같을 수 없습니다.")
    public boolean isTitleValid() {
        return !title.equals(change);
    }

}
