package festival.dev.domain.shareTDL.entity;

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
@Table(name = "share_TDL")
public class Share {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    private String title;

    private Boolean completed;

    private String startDate;

    private String endDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_name")
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "share")
    private List<Share_list> TDL_list;

    private String friends;
}