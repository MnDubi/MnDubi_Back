package festival.dev.domain.calendar.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
@Getter
@Setter
public class Calendar_tdl_ids {
    @Column(name = "toDoListId")
    private Long toDoListId;
    @Column(name = "shareId")
    private Long shareId;
    @Column(name = "groupId")
    private Long groupId;
}
