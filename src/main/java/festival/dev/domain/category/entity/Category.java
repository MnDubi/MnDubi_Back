package festival.dev.domain.category.entity;


import festival.dev.domain.TDL.entity.ToDoList;
import festival.dev.domain.gorupTDL.entity.Group;
import festival.dev.domain.shareTDL.entity.Share;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Table
@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Lob
    private String embeddingJson;


    @OneToMany(mappedBy = "category")
    private List<ToDoList> toDoLists;

    @OneToMany(mappedBy = "category")
    private List<Share> shares;

    @OneToMany(mappedBy = "category")
    private List<Group> groups;

    public Category(String name, String embeddingJson) {
        this.name = name;
        this.embeddingJson = embeddingJson;
    }

    public List<Double> getEmbeddingAsList() {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(embeddingJson, new TypeReference<List<Double>>() {});
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("임베딩 데이터를 변환할 수 없습니다.", e);
        }
    }
}
