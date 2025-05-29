package festival.dev.domain.gorupTDL.presentation.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GroupInviteDto {
    private boolean accept;
    private long groupNumber;
    private String userName;
}
