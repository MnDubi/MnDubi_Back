package festival.dev.domain.gorupTDL.presentation.dto.request;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Getter
public class GUpdateRequest {
    @NotNull
    private String title;
    @NotNull
    private String change;
    @NotNull
//    private String endDate;
//    @NotNull
//    private String changeDate;

    @AssertTrue(message = "예전 제목과 바뀐 제목, 또는 예전 날짜와 바뀐 날짜 중 하나는 달라야 합니다.")
    public boolean isValidUpdate() {
        return !title.equals(change)/* || !endDate.equals(changeDate)*/;
    }
//    @AssertTrue(message = "바꿀 날짜는 현재보다 과거 수 없습니다.")
//    public boolean isValidChange() {
//        LocalDateTime createAt = ZonedDateTime.now(ZoneId.of("Asia/Seoul")).toLocalDateTime();
//        DateTimeFormatter yearMonthDayFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");
//        return !(changeDate.compareTo(createAt.format(yearMonthDayFormatter)) < 0);
//    }
}
