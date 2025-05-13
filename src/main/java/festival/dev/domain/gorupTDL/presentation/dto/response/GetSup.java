package festival.dev.domain.gorupTDL.presentation.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GetSup {
    private Long tdlID;
    private Long all;
    private Long part;
    private String title;
    private Long groupNumber;
    private String category;
    private boolean completed;
}
