package festival.dev.domain.TDL.entity;

import festival.dev.domain.calendar.entity.Calendar;
import festival.dev.domain.category.entity.Category;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Table(name = "TDL")
@Getter
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class ToDoList {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    private String title;

    private Boolean completed;

    private String startDate;

    private String fromDate;

    @ManyToOne
    @JoinColumn(name = "category_name")
    private Category category;

    @ManyToOne
    @JoinColumn(name = "calendar_name")
    private Calendar calendar;

    private String userID;
}