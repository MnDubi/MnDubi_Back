package festival.dev.domain.gorupTDL.entity;

import festival.dev.domain.category.entity.Category;
import festival.dev.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "group_TDL")
public class Group{
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    private String title;

    private Boolean completed;

    private String startDate;

    private String endDate;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "category_name")
//    private Category category;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "user_id")
//    private User user;

    private String friends;

    @OneToMany(fetch = FetchType.LAZY,mappedBy = "group")
    private List<ToDoList_list> TDL_list;

    @Entity
    @Table(name = "GTDL_list")
    @Builder
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    static class ToDoList_list{
        @Id
        @GeneratedValue(strategy = IDENTITY)
        private Long id;

        @ManyToOne
        @JoinColumn(name = "group_tdl")
        private Group group;

        private String received;
        private String gave;
    }
}


