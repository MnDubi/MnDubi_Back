package festival.dev.domain.shareTDL.presentation.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class ShareDeleteReq {
    @NotNull
    private String title;
}
