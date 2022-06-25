package propofol.tilservice.domain.board.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import propofol.tilservice.domain.board.entity.Board;

import java.util.Collection;
import java.util.List;

public interface BoardRepository extends JpaRepository<Board, Long> {
    List<Board> findTop3ByCreatedByOrderByRecommendDesc(String createdBy);

    @Query("select b from Board b")
    @Transactional(readOnly = true)
    Page<Board> findAll(Pageable pageable);

    @Query("select b from Board b where not b.createdBy = :memberId")
    @Transactional(readOnly = true)
    Page<Board> findAllNotMine(@Param("memberId") String memberId, Pageable pageable);

    @Query(countQuery = "select b from Board b where b.createdBy =: createdBy")
    @Transactional(readOnly = true)
    Page<Board> findPagesByCreatedBy(Pageable pageable, @Param("createdBy") String createdBy);

    @Query("select b from Board b where upper(b.title) like upper(concat('%', :keyword, '%')) and not b.createdBy = :memberId")
    @Transactional(readOnly = true)
    Page<Board> findPageByTitleKeywordNotMine(@Param("memberId") String memberId, @Param("keyword")String keyword, Pageable pageable);

    @Query("select bt.board from BoardTag bt join bt.board b where bt.tagId in :tagIds " +
            "and not b.createdBy = :memberId group by bt.board.id order by count(bt) desc, bt.board.id desc")
    Page<Board> findPageByTagIdsNotMine(@Param("memberId") String memberId, @Param("tagIds") Collection<Long> tagIds, Pageable pageable);

    @Query("select bt.board from BoardTag bt join bt.board b where bt.tagId in :tagIds and upper(b.title) like upper(concat('%', :keyword, '%')) " +
            "and not b.createdBy = :memberId group by bt.board.id order by count(bt) desc, bt.board.id desc")
    Page<Board> findPagesByKeywordAndTagIdsNotMine(Pageable pageable, @Param("memberId") String memberId,
                                                      @Param("keyword") String keyword, @Param("tagIds") Collection<Long> tagIds);
}
