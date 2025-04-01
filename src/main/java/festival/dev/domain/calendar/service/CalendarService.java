package festival.dev.domain.calendar.service;

import festival.dev.domain.calendar.presentation.dto.Response.CalendarResponse;
import festival.dev.domain.calendar.presentation.dto.Response.MonthResponse;

public interface CalendarService {
    CalendarResponse getDateCalendar(String date, Long userID);
    MonthResponse getByMonth(Long userID);
}
