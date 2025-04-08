package festival.dev.domain.gorupTDL.presentation.dto.request;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import org.hibernate.validator.constraints.UniqueElements;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Getter
public class GInsertRequest {
    @NotNull
    @UniqueElements
    private List<String> titles;
    @NotNull
    private String category;
    @NotNull
    private String endDate;

    @NotNull
    @UniqueElements
    private List<String> receivers;

    @AssertTrue(message = "끝날 날짜가 현재보다 과거일 수 없습니다.")
    public boolean isValidEnd() {
        LocalDateTime createAt = ZonedDateTime.now(ZoneId.of("Asia/Seoul")).toLocalDateTime();
        DateTimeFormatter yearMonthDayFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");
        return !(endDate.compareTo(createAt.format(yearMonthDayFormatter)) < 0);
    }
}
