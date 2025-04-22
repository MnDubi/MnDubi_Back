package festival.dev.domain.gorupTDL.presentation.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GCreateWsRes {
    private String name;
    private String userCode;
    private String email;
}
