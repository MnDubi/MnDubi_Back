package festival.dev.domain.calendar.service.impl;

import festival.dev.domain.calendar.entity.Calendar;
import festival.dev.domain.calendar.presentation.dto.CalendarInsertRequest;
import festival.dev.domain.calendar.repository.CalendarRepository;
import festival.dev.domain.calendar.service.CalendarService;
import festival.dev.domain.user.entity.User;
import festival.dev.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
}
