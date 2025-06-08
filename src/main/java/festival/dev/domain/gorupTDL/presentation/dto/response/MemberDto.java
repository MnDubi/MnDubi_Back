package festival.dev.domain.gorupTDL.presentation.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MemberDto {
    private String name;
    private String email;
    private String userCode;
}
