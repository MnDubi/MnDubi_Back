package festival.dev.domain.TDL.presentation.dto.request;

import lombok.Getter;
import lombok.NonNull;

@Getter
public class DeleteRequest {
    @NonNull
    private String userID;
    @NonNull
    private String title;
}
