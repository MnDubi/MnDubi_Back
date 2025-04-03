package festival.dev.domain.user.entity;

import festival.dev.domain.TDL.entity.ToDoList;
import festival.dev.domain.calendar.entity.Calendar;
import festival.dev.domain.gorupTDL.entity.Group;
import festival.dev.domain.gorupTDL.entity.GroupList;
import festival.dev.domain.shareTDL.entity.Share;
import festival.dev.domain.shareTDL.entity.ShareList;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String name;

    @Column(nullable = true)
    private String password;

    private String provider; // OAuth 제공자 정보 (Google, Naver, Kakao)

    @Column(nullable = false)
    private String role = "USER"; // 기본값 USER

    @Column(name = "user_code", unique = true, nullable = false, updatable = false)
    private String userCode;

    @OneToMany(mappedBy = "user")
    private List<ToDoList> toDoLists;

    @OneToMany(mappedBy = "user")
    private List<Calendar> calendars;

    @OneToMany(mappedBy = "user")
    private List<Group> groups;

    @OneToMany(mappedBy = "user")
    private List<Share> shares;

    @OneToMany(mappedBy = "user")
    private List<GroupList> group_lists;

    @OneToMany(mappedBy = "user")
    private List<ShareList> share_lists;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

}
