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

    private Long groupNumber;

    @OneToMany(mappedBy = "groupNumber", orphanRemoval = true, fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    private List<Group> groups;

    @OneToMany(mappedBy = "groupNumber", orphanRemoval = true, fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    private List<GroupJoin> groupJoins;

    @OneToMany(mappedBy = "groupNumber", orphanRemoval = true, fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    private List<GroupList> groupLists;
}
