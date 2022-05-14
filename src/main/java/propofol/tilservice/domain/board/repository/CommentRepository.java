package propofol.tilservice.domain.board.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import propofol.tilservice.domain.board.entity.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query("select c from Comment c where c.board.id=:boardId group by c.groupId, c.id order by c.groupId, c.id asc")
    Page<Comment> findPageComments(@Param(value = "boardId") Long boardId, Pageable pageable);

    @Modifying
    @Query("delete from Comment c where c.board.id=:boardId")
    int deleteBulkComments(@Param("boardId") Long boardId);

    @Query("select count(c) from Comment c where c.board.id = :boardId")
    int getCommentCount(@Param("boardId") Long boardId);
}