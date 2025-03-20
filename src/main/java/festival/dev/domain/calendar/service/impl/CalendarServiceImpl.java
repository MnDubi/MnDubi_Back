package festival.dev.domain.calendar.service.impl;

import festival.dev.domain.calendar.entity.Calendar;
import festival.dev.domain.calendar.presentation.dto.CalendarInsertRequest;
import festival.dev.domain.calendar.repository.CalendarRepository;
import festival.dev.domain.calendar.service.CalendarService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CalendarServiceImpl implements CalendarService {
    private final CalendarRepository calendarRepository;
    public Calendar insert(CalendarInsertRequest request) {
        try {
            Calendar calendar = Calendar.builder()
                    .userID(request.getUserID())
                    .every(request.getEvery())
                    .part(request.getPart())
                    .build();
            return calendarRepository.save(calendar);
        }catch (Exception e){
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    public Calendar getDateCalendar(String date, String userID){
        try{
            return calendarRepository.findByFormattedDateAndUserID(date, userID);
        }catch (Exception e){
            throw new IllegalArgumentException(e.getMessage());
        }
    }
}
