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
@Table(name = "group_tdl")
@Getter
public class Group {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    private String title;

    private Boolean completed;

    private String startDate;

    private String endDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_number")
    private GroupNumber groupNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id")
    private User user;

    @OneToMany(fetch = FetchType.LAZY,mappedBy = "group")
    private List<GroupList> TDL_list;
}


