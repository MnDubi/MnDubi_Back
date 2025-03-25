package festival.dev.domain.calendar.service.impl;

import festival.dev.domain.TDL.entity.ToDoList;
import festival.dev.domain.TDL.repository.ToDoListRepository;
import festival.dev.domain.calendar.entity.Calendar;
import festival.dev.domain.calendar.presentation.dto.CalendarInsertRequest;
import festival.dev.domain.calendar.repository.CalendarRepository;
import festival.dev.domain.calendar.service.CalendarService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CalendarServiceImpl implements CalendarService {

    private final CalendarRepository calendarRepository;
    private final ToDoListRepository toDoListRepository;

    public Calendar insert(CalendarInsertRequest request) {
        try {
            LocalDateTime createAt = ZonedDateTime.now(ZoneId.of("Asia/Seoul")).toLocalDateTime();
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MM.dd");
            String formattedDate = createAt.format(dateFormatter);

//            List<ToDoList> tdl = toDoListRepository.findByFormattedDate(formattedDate);
            Calendar calendar = Calendar.builder()
                    .userID(request.getUserID())
                    .every(request.getEvery())
                    .part(request.getPart())
//                    .toDoLists(tdl)
                    .build();
            return calendarRepository.save(calendar);
        }catch (Exception e){
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    public Calendar getDateCalendar(String date, String userID){
        try{
            return calendarRepository.findByYearMonthDayAndUserID(date, userID);
        }catch (Exception e){
            throw new IllegalArgumentException(e.getMessage());
        }
    }
}
