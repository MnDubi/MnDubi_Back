package festival.dev.domain.TDL.entity;

import festival.dev.domain.category.entity.Category;
import festival.dev.global.entity.BaseTime;
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
public class ToDoList extends BaseTime {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    private String title;

    private Boolean completed;

    @ManyToOne
    @JoinColumn(name = "category_name")
    private Category category;

    private String userID;
}