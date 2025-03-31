package festival.dev.domain.calendar.presentation.dto.Response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CalendarDtoAsis{
    private String endDate;
    private String startDate;
    private String title;
    private boolean completed;
    private String category;
}
