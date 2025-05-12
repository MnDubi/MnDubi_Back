package festival.dev.domain.shareTDL.presentation.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ShareJoinRes {
    private Long shareNumber;
    private String title;
    private boolean completed;
    private String category;
    private String user_code;
}
