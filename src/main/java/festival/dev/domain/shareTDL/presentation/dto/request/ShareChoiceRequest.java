package festival.dev.domain.shareTDL.presentation.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ShareChoiceRequest {
    @NotNull
    private Long shareNumber;
}
