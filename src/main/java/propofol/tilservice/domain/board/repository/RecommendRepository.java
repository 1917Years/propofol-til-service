package propofol.tilservice.domain.board.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import propofol.tilservice.domain.board.entity.Recommend;

public interface RecommendRepository extends JpaRepository<Recommend, Long> {

    @Modifying
    @Query(value = "delete from Recommend r where r.board.id=:boardId")
    int deleteBulkRecommends(@Param(value = "boardId") Long boardId);
}
