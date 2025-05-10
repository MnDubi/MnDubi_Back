package festival.dev.domain.calendar.service;

import festival.dev.domain.calendar.presentation.dto.Response.CalendarResponse;
import festival.dev.domain.calendar.presentation.dto.Response.MonthResponse;

public interface CalendarService {
    CalendarResponse getDateCalendarWithPrivate(String date, Long userID);
    MonthResponse getByMonthWithPrivate(Long userID);
    CalendarResponse getDateCalendarWithGroup(String date, Long userID);
    MonthResponse getByMonthWithGroup(Long userID);
    MonthResponse getByMonthWithShare(Long userID);
    CalendarResponse getDateCalendarWithShare(String date,Long userID);
}
