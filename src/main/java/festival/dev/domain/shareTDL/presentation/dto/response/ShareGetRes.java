package festival.dev.domain.shareTDL.presentation.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ShareGetRes {
    private String username;
    private ShareJoinRes shareJoinRes;
}
