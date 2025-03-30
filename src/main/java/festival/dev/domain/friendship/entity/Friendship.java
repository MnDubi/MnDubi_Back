package festival.dev.domain.friendship.entity;

import festival.dev.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Friendship {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private User requester; // 요청자

    @ManyToOne(optional = false)
    private User addressee; // 친구 대상

    private LocalDateTime createdAt;
}
