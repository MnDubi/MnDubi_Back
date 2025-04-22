package festival.dev.domain.gorupTDL.presentation.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GInviteGet {
    private String sender;
    private boolean accepted;
    private Long groupNumber;
    private String receiver;
}
