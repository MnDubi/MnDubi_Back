package festival.dev.domain.TDL.presentation.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ToDoListResponse {
    private String formattedDate;
    private String userID;
    private String title;
    private boolean completed;
    private String category;
}
