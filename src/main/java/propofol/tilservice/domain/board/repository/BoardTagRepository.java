package propofol.tilservice.domain.board.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import propofol.tilservice.domain.board.entity.BoardTag;

public interface BoardTagRepository extends JpaRepository<BoardTag, Long> {

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("delete from BoardTag bt where bt.board.id = :boardId")
    void deleteAllByBoardId(@Param("boardId") Long boardId);
}
