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
    private String ownerName;
    private String title;
    private Long groupNumber;
    private String category;
}
