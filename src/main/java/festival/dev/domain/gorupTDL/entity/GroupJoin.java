package festival.dev.domain.gorupTDL.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "group_join")
public class GroupJoin {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "group_list_id")
    private GroupList groupList;

    @ManyToOne
    @JoinColumn(name="group_number_id")
    private GroupNumber groupNumber;
}
