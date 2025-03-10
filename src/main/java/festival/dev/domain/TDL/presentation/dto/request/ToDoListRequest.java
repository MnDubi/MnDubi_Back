package festival.dev.domain.TDL.presentation.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Getter
public class ToDoListRequest {
    @NonNull
    private String title;
    @NonNull
    private Boolean completed;
    @NonNull
    private String userID;
    @NonNull
    private String category;
}
