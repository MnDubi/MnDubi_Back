package festival.dev.domain.gorupTDL.entity;

import festival.dev.domain.calendar.entity.Calendar;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Entity
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Table
@Getter
public class GroupCalendar {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    private Calendar calendar;
}
