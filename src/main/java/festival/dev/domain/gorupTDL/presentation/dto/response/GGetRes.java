package festival.dev.domain.gorupTDL.presentation.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder(toBuilder = true)
public class GGetRes {
    private Long all;
    private Long part;
    private String name;
    private List<GetSup> getSups;
}
