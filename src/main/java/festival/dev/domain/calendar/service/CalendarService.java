package festival.dev.domain.calendar.service;

import festival.dev.domain.calendar.entity.Calendar;
import festival.dev.domain.calendar.presentation.dto.CalendarInsertRequest;

public interface CalendarService {
    Calendar insert(CalendarInsertRequest request);
    Calendar getDateCalendar(String date, String userID);
}
