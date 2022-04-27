package propofol.tilservice.domain.board.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import propofol.tilservice.domain.board.entity.Board;

public interface BoardRepository extends JpaRepository<Board, Long> {
    @Query("select b from Board b")
    Page<Board> findAll(Pageable pageable);
}
