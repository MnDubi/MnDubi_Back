package festival.dev.domain.gorupTDL.entity;

import festival.dev.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Table(name = "GTDL_list")
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Group_list{
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "group_tdl")
    private Group group;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "received")
    private User user;
}