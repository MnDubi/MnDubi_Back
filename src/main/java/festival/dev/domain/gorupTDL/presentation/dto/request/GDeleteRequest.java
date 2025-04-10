package festival.dev.domain.gorupTDL.presentation.dto.request;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Getter
public class GDeleteRequest {
    @NotNull
    private String title;
    @NotNull
    private String endDate;

    @AssertTrue(message = "이미 끝난 ToDoList는 삭제가 불가능합니다.")
    public boolean isValidEnd() {
        LocalDateTime createAt = ZonedDateTime.now(ZoneId.of("Asia/Seoul")).toLocalDateTime();
        DateTimeFormatter yearMonthDayFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");
        return !(endDate.compareTo(createAt.format(yearMonthDayFormatter)) < 0);
    }

}
