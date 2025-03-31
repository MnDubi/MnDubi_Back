package festival.dev.domain.TDL.presentation.dto.request;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Getter
public class InsertUntilRequest {
    @NotNull
    private String title;
    @NotNull
    private String category;
    @NotNull
    private String startDate;
    @NotNull
    private String endDate;

    @AssertTrue(message = "시작하는 날짜는 현재 날짜보다 과거일 수 없습니다.")
    public boolean isValid() {
        LocalDateTime createAt = ZonedDateTime.now(ZoneId.of("Asia/Seoul")).toLocalDateTime();
        DateTimeFormatter yearMonthDayFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");
        return !(startDate.compareTo(createAt.format(yearMonthDayFormatter)) < 0);
    }
}
