package propofol.tilservice.domain.board.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import propofol.tilservice.domain.board.entity.Board;
import propofol.tilservice.domain.board.entity.Comment;
import propofol.tilservice.domain.board.repository.BoardRepository;
import propofol.tilservice.domain.board.repository.CommentRepository;
import propofol.tilservice.domain.board.service.dto.CommentDto;
import propofol.tilservice.domain.exception.NotFoundBoardException;
import propofol.tilservice.domain.exception.NotFoundCommentException;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentService {
    private final CommentRepository commentRepository;
    private final BoardRepository boardRepository;

    @Transactional
    public String saveParentComment(CommentDto commentDto, Long boardId) {
        Board findBoard = boardRepository.findById(boardId).orElseThrow(() -> {
            throw new NotFoundBoardException("게시글을 찾을 수 없습니다.");
        });

        Comment comment = Comment.createComment()
                .content(commentDto.getContent())
                .board(findBoard)
                .build();

        findBoard.addComment(comment);
        return "ok";
    }

    @Transactional
    public String saveChildComment(CommentDto commentDto, Long boardId, Long parentId) {
        Board findBoard = boardRepository.findById(boardId).orElseThrow(() -> {
            throw new NotFoundBoardException("게시글을 찾을 수 없습니다.");
        });

        Comment comment = Comment.createComment()
                .content(commentDto.getContent())
                .board(findBoard)
                .build();

        Comment parentComment = commentRepository.findById(parentId).orElseThrow(() -> {
            throw new NotFoundCommentException("댓글을 찾을 수 없습니다.");
        });

        comment.setParent(parentComment);
        findBoard.addComment(comment);
        return "ok";

    }
}
