package festival.dev.domain.calendar.entity;

import festival.dev.domain.user.entity.User;
import festival.dev.global.entity.BaseTime;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class Calendar extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int every;

    private int part;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}