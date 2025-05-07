package festival.dev.domain.shareTDL.presentation.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class ShareGetRes {
    private String username;
    private Long every;
    private Long part;
    private List<ShareJoinRes> shareJoinRes;
}
