package festival.dev.domain.calendar.presentation.dto;

import lombok.Getter;

@Getter
public class CalendarInsertRequest {
    private String userID;
    private int every;
    private int part;
}