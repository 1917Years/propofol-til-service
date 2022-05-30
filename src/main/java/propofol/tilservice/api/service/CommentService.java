package propofol.tilservice.api.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import propofol.tilservice.api.controller.dto.CommentRequestDto;
import propofol.tilservice.api.feign.service.AlarmService;
import propofol.tilservice.api.feign.service.UserService;
import propofol.tilservice.domain.board.entity.Board;
import propofol.tilservice.domain.board.entity.Comment;
import propofol.tilservice.domain.board.repository.BoardRepository;
import propofol.tilservice.domain.board.repository.CommentRepository;
import propofol.tilservice.domain.exception.NotFoundBoardException;
import propofol.tilservice.domain.exception.NotFoundCommentException;

import static propofol.tilservice.api.feign.AlarmType.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentService {
    private final CommentRepository commentRepository;
    private final BoardRepository boardRepository;
    private final UserService userService;
    private final AlarmService alarmService;

    @Transactional
    public Comment saveParentComment(CommentRequestDto commentDto, Long boardId, String token, String memberId) {
        Board findBoard = boardRepository.findById(boardId).orElseThrow(() -> {
            throw new NotFoundBoardException("게시글을 찾을 수 없습니다.");
        });

        String userNickName = userService.getUserNickName(token, memberId);
        Comment comment = Comment.createComment()
                .content(commentDto.getContent())
                .memberId(Long.parseLong(memberId))
                .nickname(userNickName)
                .board(findBoard)
                .build();

        Comment saveComment = commentRepository.save(comment);
        saveComment.addGroupInfo(saveComment.getId());

        alarmService.saveAlarm(Long.parseLong(findBoard.getContent()),
                "등록된 글 " + findBoard.getTitle() + "에 새로운 댓글이 추가되었습니다.", token, COMMENT);

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
                .memberId(Long.parseLong(memberId))
                .nickname(userNickName)
                .board(findBoard)
                .build();
        comment.addGroupInfo(parentId);

        Comment parentComment = findById(parentId);

        alarmService.saveAlarm(parentComment.getMemberId(),
                "등록된 글 " + findBoard.getTitle() + "에 새로운 답글이 추가되었습니다.", token, COMMENT);

        return commentRepository.save(comment);
    }

    public Comment findById(Long id){
        return commentRepository.findById(id)
                .orElseThrow(() -> {throw new NotFoundCommentException("등록된 댓글이 없습니다.");});
    }

    public Page<Comment> getComments(Long boardId, Integer page) {
        return getPageComments(boardId, page);
    }

    public int getCommentCount(Long boardId) {return commentRepository.getCommentCount(boardId);}

    private Page<Comment> getPageComments(Long boardId, Integer page) {
        PageRequest pageRequest = PageRequest.of(page - 1, 10, Sort.by(Sort.Direction.ASC, "id"));
        return commentRepository.findPageComments(boardId, pageRequest);
    }

    public String deleteComment(Long commentId) {
        Comment findComment = commentRepository.findById(commentId).orElseThrow(() -> {
            throw new NotFoundCommentException("댓글을 조회할 수 없습니다.");
        });

        if(findComment.getGroupId() == commentId){
            commentRepository.deleteBulkCommentsByGroupId(findComment.getGroupId());
        }else{
            commentRepository.delete(findComment);
        }
        return "ok";
    }
}