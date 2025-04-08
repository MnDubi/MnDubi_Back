package festival.dev.domain.category.repository;

import festival.dev.domain.category.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.*;
import java.util.*;


import java.util.Optional;
@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findByCategoryName(String categoryName);
    boolean existsByCategoryName(String name);
    void deleteByCategoryName(String categoryName);
    Optional<Category> findByName(String name);

    @Query("SELECT c.name, c.embeddingJson FROM Category c")
    List<Object[]> findAllNameAndEmbedding();
}
