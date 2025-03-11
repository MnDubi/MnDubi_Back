package festival.dev.domain.TDL.presentation.dto.request;

import lombok.Getter;
import lombok.NonNull;

@Getter
public class InsertRequest {
    @NonNull
    private String title;
    @NonNull
    private String userID;
    @NonNull
    private String category;
}
