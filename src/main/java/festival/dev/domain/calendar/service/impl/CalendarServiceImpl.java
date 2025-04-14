package festival.dev.domain.calendar.service.impl;

import festival.dev.domain.TDL.entity.ToDoList;
import festival.dev.domain.TDL.repository.ToDoListRepository;
import festival.dev.domain.calendar.entity.Calendar;
import festival.dev.domain.calendar.entity.Calendar_tdl_ids;
import festival.dev.domain.calendar.entity.TdlKind;
import festival.dev.domain.calendar.presentation.dto.Response.CalendarDtoAsis;
import festival.dev.domain.calendar.presentation.dto.Response.CalendarResponse;
import festival.dev.domain.calendar.presentation.dto.Response.MonthResponse;
import festival.dev.domain.calendar.repository.CalendarRepository;
import festival.dev.domain.calendar.service.CalendarService;
import festival.dev.domain.user.entity.User;
import festival.dev.domain.user.repository.UserRepository;
import jakarta.persistence.Tuple;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CalendarServiceImpl implements CalendarService {

    private final CalendarRepository calendarRepository;
    private final UserRepository userRepository;
    private final ToDoListRepository toDoListRepository;

    private final Logger LOGGER = LoggerFactory.getLogger(CalendarServiceImpl.class);

    public CalendarResponse getDateCalendarWithPrivate(String date, Long userID){
        return getDateCalendar(date, userID, TdlKind.PRIVATE);
    }

    public MonthResponse getByMonthWithPrivate(Long userID){
        return getByMonth(userID,TdlKind.PRIVATE);
    }

    public CalendarResponse getDateCalendarWithGroup(String date, Long userID){
        return getDateCalendar(date, userID, TdlKind.GROUP);
    }

    public MonthResponse getByMonthWithGroup(Long userID){
        return getByMonth(userID,TdlKind.GROUP);
    }

    CalendarResponse getDateCalendar(String date, Long userID, TdlKind tdlKind){
        try{
            User user = userGet(userID);
            Calendar calendar = calendarRepository.findWithTDLIDsByUserDateKind(user.getId(),date,tdlKind).orElseThrow(()-> new IllegalArgumentException("캘린더에 데이터가 존재하지 않습니다."));
            List<Calendar_tdl_ids> tdlIds = calendar.getToDoListId();

            List<ToDoList> tdls =  toDoListRepository.findByIdIn(tdlIds.stream()
                    .map(Calendar_tdl_ids::getTdlID)
                    .collect(Collectors.toList()));

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

    MonthResponse getByMonth(Long userID, TdlKind tdlKind) {
        LocalDateTime createAt = ZonedDateTime.now(ZoneId.of("Asia/Seoul")).toLocalDateTime();

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MM");
        String month = createAt.format(dateFormatter);

        User user = userGet(userID);

        List<Tuple> result = calendarRepository.findByMonth(month, userID, tdlKind);

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
