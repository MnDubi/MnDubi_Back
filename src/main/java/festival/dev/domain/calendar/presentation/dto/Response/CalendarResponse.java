package festival.dev.domain.calendar.presentation.dto.Response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class CalendarResponse {
    private List<CalendarDtoAsis> tdl;

    private String username;
    private int every;
    private int part;
    private String day_of_week;
    private String year_month_day;
}

