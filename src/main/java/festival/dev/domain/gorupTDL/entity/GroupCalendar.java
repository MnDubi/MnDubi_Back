package festival.dev.domain.gorupTDL.entity;

import jakarta.persistence.*;
import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
@Getter
@Setter
public class GroupCalendar {
    private String title;
    private Long category;
}
