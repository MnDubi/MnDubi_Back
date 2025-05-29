package festival.dev.domain.shareTDL.presentation.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ShareInviteDto {
    private boolean accept;
    private long shareNumber;
    private String userName;
}
