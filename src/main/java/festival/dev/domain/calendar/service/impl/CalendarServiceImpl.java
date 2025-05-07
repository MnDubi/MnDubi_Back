package festival.dev.domain.calendar.service.impl;

import festival.dev.domain.TDL.entity.ToDoList;
import festival.dev.domain.TDL.repository.ToDoListRepository;
import festival.dev.domain.calendar.entity.Calendar;
import festival.dev.domain.calendar.entity.Calendar_tdl_ids;
import festival.dev.domain.calendar.entity.CTdlKind;
import festival.dev.domain.calendar.presentation.dto.Response.CalendarDtoAsis;
import festival.dev.domain.calendar.presentation.dto.Response.CalendarResponse;
import festival.dev.domain.calendar.presentation.dto.Response.MonthResponse;
import festival.dev.domain.calendar.repository.CalendarRepository;
import festival.dev.domain.calendar.service.CalendarService;
import festival.dev.domain.category.entity.Category;
import festival.dev.domain.category.repository.CategoryRepository;
import festival.dev.domain.gorupTDL.entity.GroupCalendar;
import festival.dev.domain.user.entity.User;
import festival.dev.domain.user.repository.UserRepository;
import jakarta.persistence.Tuple;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CalendarServiceImpl implements CalendarService {

    private final CalendarRepository calendarRepository;
    private final UserRepository userRepository;
    private final ToDoListRepository toDoListRepository;
    private final CategoryRepository categoryRepository;

    public CalendarResponse getDateCalendarWithPrivate(String date, Long userID){
        return getDateCalendar(date, userID, CTdlKind.PRIVATE);
    }

    public MonthResponse getByMonthWithPrivate(Long userID){
        return getByMonth(userID, CTdlKind.PRIVATE);
    }

    public CalendarResponse getDateCalendarWithGroup(String date, Long userID){
        return getDateCalendar(date, userID, CTdlKind.GROUP);
    }

    public MonthResponse getByMonthWithGroup(Long userID){
        return getByMonth(userID, CTdlKind.GROUP);
    }

    CalendarResponse getDateCalendar(String date, Long userID, CTdlKind CTdlKind){
        try{
            User user = userGet(userID);
            Calendar calendar = calendarRepository.findWithTDLIDsByUserDateKind(user.getId(),date, CTdlKind).orElseThrow(()-> new IllegalArgumentException("캘린더에 데이터가 존재하지 않습니다."));
            List<CalendarDtoAsis> tdl = tdl(CTdlKind,calendar);

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

    MonthResponse getByMonth(Long userID, CTdlKind CTdlKind) {
        LocalDateTime createAt = ZonedDateTime.now(ZoneId.of("Asia/Seoul")).toLocalDateTime();

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MM");
        String month = createAt.format(dateFormatter);

        User user = userGet(userID);

        List<Tuple> result = calendarRepository.findByMonth(month, userID, CTdlKind);

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

    List<CalendarDtoAsis> tdl(CTdlKind kind, Calendar calendar) {
        String endDate;
        String startDate;
        String title;
        boolean completed;
        String category;

        List<CalendarDtoAsis> response = new ArrayList<>(List.of());
        switch (kind) {
            case PRIVATE,SHARE -> {
                List<Calendar_tdl_ids> tdlIds = calendar.getToDoListId();

                List<Long> tdlIdList = tdlIds.stream()
                        .map(Calendar_tdl_ids::getTdlID)
                        .collect(Collectors.toList());

                List<ToDoList> toDoLists = toDoListRepository.findByIdIn(tdlIdList);

                for (ToDoList tdl : toDoLists) {
                    title = tdl.getTitle();
                    startDate = tdl.getStartDate();
                    endDate = tdl.getEndDate();
                    completed = tdl.getCompleted();
                    category = tdl.getCategory().getCategoryName();

                    response.add(CalendarDtoAsis.builder()
                            .title(title)
                            .startDate(startDate)
                            .endDate(endDate)
                            .category(category)
                            .completed(completed)
                            .build());
                }

            }
            case GROUP ->{
                List<GroupCalendar> tdlIds = calendar.getGroupCalendarId();

                List<Long> categoryIds = tdlIds.stream()
                        .map(GroupCalendar::getCategory)
                        .collect(Collectors.toList());

                Map<Long, String> categoryMap = categoryRepository.findAllById(categoryIds).stream()
                        .collect(Collectors.toMap(Category::getId, Category::getCategoryName));

                for (GroupCalendar tdl : tdlIds) {
                    title = tdl.getTitle();
                    category = categoryMap.getOrDefault(tdl.getCategory(), "알 수 없음");
                    startDate = "Group";
                    endDate = "Group";
                    completed = tdl.isCompleted();

                    response.add(CalendarDtoAsis.builder()
                            .title(title)
                            .startDate(startDate)
                            .endDate(endDate)
                            .category(category)
                            .completed(completed)
                            .build());
                }
            }
        }
        return response;
    }

    User userGet(Long userID){
        return userRepository.findById(userID).orElseThrow(() -> new IllegalArgumentException("없는 UserID 입니다."));
    }
}
