package festival.dev.domain.calendar.service.impl;

import festival.dev.domain.TDL.entity.ToDoList;
import festival.dev.domain.TDL.repository.ToDoListRepository;
import festival.dev.domain.calendar.entity.Calendar;
import festival.dev.domain.calendar.presentation.dto.Response.CalendarDtoAsis;
import festival.dev.domain.calendar.presentation.dto.Response.CalendarResponse;
import festival.dev.domain.calendar.presentation.dto.Response.MonthResponse;
import festival.dev.domain.calendar.repository.CalendarRepository;
import festival.dev.domain.calendar.service.CalendarService;
import festival.dev.domain.user.entity.User;
import festival.dev.domain.user.repository.UserRepository;
import jakarta.persistence.Tuple;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CalendarServiceImpl implements CalendarService {

    private final CalendarRepository calendarRepository;
    private final UserRepository userRepository;
    private final ToDoListRepository toDoListRepository;

    public CalendarResponse getDateCalendar(String date, Long userID){
        try{
            User user = userGet(userID);
            Calendar calendar = calendarRepository.findByYearMonthDayAndUser(date, user);
            Collection<Long> tdlIds = calendar.getToDoListId();
            List<ToDoList> tdls =  toDoListRepository.findByIdIn(tdlIds);
            List<CalendarDtoAsis> tdl = tdls.stream()
                    .map(toDoList -> CalendarDtoAsis.builder()
                            .title(toDoList.getTitle())
                            .startDate(toDoList.getStartDate())
                            .endDate(toDoList.getEndDate())
                            .completed(toDoList.getCompleted())
                            .category(toDoList.getCategory().getCategoryName())
                            .build()).toList();

            return CalendarResponse.builder()
                    .tdl(tdl)
                    .username(user.getName())
                    .day_of_week(calendar.getDayOfWeek())
                    .year_month_day(calendar.getYearMonthDay())
                    .every(calendar.getEvery())
                    .part(calendar.getPart())
                    .build();
        }catch (Exception e){
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    public MonthResponse getByMonth(Long userID) {
        LocalDateTime createAt = ZonedDateTime.now(ZoneId.of("Asia/Seoul")).toLocalDateTime();

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MM");
        String month = createAt.format(dateFormatter);

        User user = userGet(userID);

        List<Tuple> result = calendarRepository.findByMonth(month, userID);

        Tuple tuple = result.get(0);
        Long monthEvery = tuple.get("monthEvery", Number.class) != null ? tuple.get("monthEvery", Number.class).longValue() : 0L;
        Long monthPart = tuple.get("monthPart", Number.class) != null ? tuple.get("monthPart", Number.class).longValue() : 0L;

        return MonthResponse.builder()
                .month(month)
                .username(user.getName())
                .every(monthEvery)
                .part(monthPart)
                .build();
    }

    public User userGet(Long userID){
        return userRepository.findById(userID).orElseThrow(() -> new IllegalArgumentException("없는 UserID 입니다."));
    }
}
