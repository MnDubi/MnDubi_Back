package festival.dev.domain.calendar.entity;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
@Getter
@Setter
public class Calendar_tdl_ids {
    private Long tdlID;
    @Enumerated(EnumType.STRING)
    private TdlKind kind;
}
