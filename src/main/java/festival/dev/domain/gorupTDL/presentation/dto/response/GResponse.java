package festival.dev.domain.gorupTDL.presentation.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GResponse {
    private String ownerName;
    private String title;
    private boolean completed;
    private Long groupNumber;
    private String category;
    private String memberName;
}
