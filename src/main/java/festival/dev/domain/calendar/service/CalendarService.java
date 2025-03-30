package festival.dev.domain.calendar.service;

import festival.dev.domain.calendar.entity.Calendar;
import festival.dev.domain.calendar.presentation.dto.Request.CalendarInsertRequest;
import festival.dev.domain.calendar.presentation.dto.Response.MonthResponse;

public interface CalendarService {
    Calendar insert(CalendarInsertRequest request, Long userID);
    Calendar getDateCalendar(String date, Long userID);
    MonthResponse getByMonth(Long userID);
}
