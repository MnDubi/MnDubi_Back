package festival.dev.domain.gorupTDL.presentation.dto.request;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Getter
public class GInsertRequest {
    @NotNull
    private String title;
    @NotNull
    private String category;
    @NotNull
    private String endDate;

    @AssertTrue(message = "끝날 날짜가 현재보다 과거일 수 없습니다.")
    public boolean isValidEnd() {
        LocalDateTime createAt = ZonedDateTime.now(ZoneId.of("Asia/Seoul")).toLocalDateTime();
        DateTimeFormatter yearMonthDayFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");
        return !(endDate.compareTo(createAt.format(yearMonthDayFormatter)) < 0);
    }
}
