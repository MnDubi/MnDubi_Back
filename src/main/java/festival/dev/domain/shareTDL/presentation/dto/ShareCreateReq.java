package festival.dev.domain.shareTDL.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ShareCreateReq {
    private List<String> userCode;
    private boolean showShared;
    private boolean includeShared;
}
