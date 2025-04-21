package festival.dev.domain.shareTDL.entity;

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
public class ShareNumber {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long number;

    @OneToMany(mappedBy = "shareNumber", orphanRemoval = true, fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    private List<Share> shares;
}
