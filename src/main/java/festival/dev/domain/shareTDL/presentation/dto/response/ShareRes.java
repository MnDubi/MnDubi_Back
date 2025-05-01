package festival.dev.domain.shareTDL.presentation.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ShareRes {
    private String title;
    private String category;
    private Long shareNumber;
    private Boolean accept;
}
