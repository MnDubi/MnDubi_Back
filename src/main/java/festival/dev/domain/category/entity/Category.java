package festival.dev.domain.category.entity;

import festival.dev.domain.TDL.entity.ToDoList;
import festival.dev.domain.gorupTDL.entity.Group;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Table
@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String categoryName;

    private String keyword;

    @OneToMany(mappedBy = "category")
    private List<ToDoList> toDoLists;

    @OneToMany(mappedBy = "category")
    private List<Group> groups;
}
