package propofol.tilservice.domain.board.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import propofol.tilservice.domain.board.entity.Recommend;

public interface RecommendRepository extends JpaRepository<Recommend, Long> {
}
