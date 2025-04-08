package festival.dev.domain.gorupTDL.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class GroupNumber {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private Long groupNumber;

    @OneToMany(mappedBy = "groupNumber", orphanRemoval = true)
    private List<Group> groups;

    @OneToMany(mappedBy = "groupNumber", orphanRemoval = true)
    private List<GroupJoin> groupJoins;
}
