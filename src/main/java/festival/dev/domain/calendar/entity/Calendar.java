package festival.dev.domain.calendar.entity;

import festival.dev.domain.TDL.entity.ToDoList;
import festival.dev.global.entity.BaseTime;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class Calendar extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userID;

    private int every;

    private int part;

    @OneToMany(mappedBy = "calendar")
    private List<ToDoList> toDoLists;
}