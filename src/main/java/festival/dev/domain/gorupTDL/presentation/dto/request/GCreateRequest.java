package festival.dev.domain.gorupTDL.presentation.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import org.hibernate.validator.constraints.UniqueElements;

import java.util.List;

@Getter
public class GCreateRequest {
    @NotNull
    @UniqueElements
    private List<String> titles;
    @NotNull
    private String category;

    @NotNull
    @UniqueElements
    private List<String> receivers;
}
