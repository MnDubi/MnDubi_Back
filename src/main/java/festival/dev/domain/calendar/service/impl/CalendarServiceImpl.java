package festival.dev.domain.calendar.service.impl;

import festival.dev.domain.calendar.entity.Calendar;
import festival.dev.domain.calendar.presentation.dto.Request.CalendarInsertRequest;
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
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CalendarServiceImpl implements CalendarService {

    private final CalendarRepository calendarRepository;
    private final UserRepository userRepository;

    public Calendar insert(CalendarInsertRequest request, Long userID) {
        try {
            User user = userGet(userID);
            Calendar calendar = Calendar.builder()
                    .user(user)
                    .every(request.getEvery())
                    .part(request.getPart())
                    .build();

            return calendarRepository.save(calendar);
        }catch (Exception e){
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    public Calendar getDateCalendar(String date, Long userID){
        try{
            User user = userGet(userID);
            return calendarRepository.findByYearMonthDayAndUser(date, user);
        }catch (Exception e){
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    public User userGet(Long userID){
        return userRepository.findById(userID).orElseThrow(() -> new IllegalArgumentException("없는 UserID 입니다."));
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

}
