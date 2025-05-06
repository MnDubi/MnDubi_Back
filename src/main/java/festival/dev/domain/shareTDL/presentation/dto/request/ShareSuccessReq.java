package festival.dev.domain.shareTDL.presentation.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class ShareSuccessReq {
    @NotNull
    private String title;
    @NotNull
    private boolean success;
}
