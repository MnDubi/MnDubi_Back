package festival.dev.domain.shareTDL.presentation.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ShareUserList {
    private String name;
    private String userCode;
    private String email;
}
