package festival.dev.domain.gorupTDL.entity;

import festival.dev.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Table(name = "GTDL_list")
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class GroupList {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_number")
    private GroupNumber groupNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "received_id")
    private User user;

    private boolean accept;
}