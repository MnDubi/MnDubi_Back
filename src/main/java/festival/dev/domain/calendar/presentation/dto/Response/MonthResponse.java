package festival.dev.domain.calendar.presentation.dto.Response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MonthResponse {
    private String month;
    private Long every;
    private Long part;
    private String username;
}
