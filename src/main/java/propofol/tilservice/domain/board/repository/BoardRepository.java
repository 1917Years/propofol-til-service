package propofol.tilservice.domain.board.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import propofol.tilservice.domain.board.entity.Board;

public interface BoardRepository extends JpaRepository<Board, Long> {
    @Query("select b from Board b")
    @Transactional(readOnly = true)
    Page<Board> findAll(Pageable pageable);

    @Query(countQuery = "select b from Board b where b.createdBy =: createdBy")
    @Transactional(readOnly = true)
    Page<Board> findPagesByCreatedBy(Pageable pageable, @Param("createdBy") String createdBy);
}
