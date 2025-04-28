package festival.dev.domain.shareTDL.entity;

import festival.dev.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "share")
@Getter
public class Share {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "share_number")
    private ShareNumber shareNumber;

    @OneToMany(mappedBy = "share", orphanRemoval = true, fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    private List<ShareJoin> shareJoins;

    private boolean accepted;
    private boolean owner;
    private boolean showShared;
    private boolean includeShared;
}