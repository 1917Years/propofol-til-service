package propofol.tilservice.api.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import propofol.tilservice.api.controller.dto.CommentRequestDto;
import propofol.tilservice.api.feign.service.UserService;
import propofol.tilservice.domain.board.entity.Board;
import propofol.tilservice.domain.board.entity.Comment;
import propofol.tilservice.domain.board.repository.BoardRepository;
import propofol.tilservice.domain.board.repository.CommentRepository;
import propofol.tilservice.domain.exception.NotFoundBoardException;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentService {
    private final CommentRepository commentRepository;
    private final BoardRepository boardRepository;
    private final UserService userService;

    @Transactional
    public Comment saveParentComment(CommentRequestDto commentDto, Long boardId, String token, String memberId) {
        Board findBoard = boardRepository.findById(boardId).orElseThrow(() -> {
            throw new NotFoundBoardException("게시글을 찾을 수 없습니다.");
        });

        String userNickName = userService.getUserNickName(token, memberId);
        Comment comment = Comment.createComment()
                .content(commentDto.getContent())
                .nickname(userNickName)
                .board(findBoard)
                .build();

        Comment saveComment = commentRepository.save(comment);
        saveComment.addGroupInfo(saveComment.getId());

        return saveComment;
    }

    @Transactional
    public Comment saveChildComment(CommentRequestDto commentDto, Long boardId, Long parentId,
                                    String token, String memberId) {
        Board findBoard = boardRepository.findById(boardId).orElseThrow(() -> {
            throw new NotFoundBoardException("게시글을 찾을 수 없습니다.");
        });

        String userNickName = userService.getUserNickName(token, memberId);
        Comment comment = Comment.createComment()
                .content(commentDto.getContent())
                .nickname(userNickName)
                .board(findBoard)
                .build();
        comment.addGroupInfo(parentId);
        return commentRepository.save(comment);
    }

    public Page<Comment> getComments(Long boardId, Integer page) {
        return getPageComments(boardId, page);
    }

    public int getCommentCount(Long boardId) {return commentRepository.getCommentCount(boardId);}

    private Page<Comment> getPageComments(Long boardId, Integer page) {
        PageRequest pageRequest = PageRequest.of(page - 1, 10, Sort.by(Sort.Direction.ASC, "id"));
        return commentRepository.findPageComments(boardId, pageRequest);
    }
}