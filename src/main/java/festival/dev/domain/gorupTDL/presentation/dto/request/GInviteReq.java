package festival.dev.domain.gorupTDL.presentation.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.util.List;

@Getter
public class GInviteReq {
    @NotNull
    private List<String> receivers;
    @NotNull
    private Long groupID;
}
