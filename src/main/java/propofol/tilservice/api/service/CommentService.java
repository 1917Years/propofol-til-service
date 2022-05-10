package propofol.tilservice.api.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import propofol.tilservice.api.controller.dto.CommentRequestDto;
import propofol.tilservice.api.feign.UserServiceFeignClient;
import propofol.tilservice.api.feign.dto.MemberInfoDto;
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
    private final UserServiceFeignClient userServiceFeignClient;

    @Transactional
    public String saveParentComment(CommentRequestDto commentDto, Long boardId, String token) {
        Board findBoard = boardRepository.findById(boardId).orElseThrow(() -> {
            throw new NotFoundBoardException("게시글을 찾을 수 없습니다.");
        });

        MemberInfoDto memberInfo = userServiceFeignClient.getMemberInfo(token);
        Comment comment = Comment.createComment()
                .content(commentDto.getContent())
                .nickname(memberInfo.getNickname())
                .board(findBoard)
                .build();

        Comment saveComment = commentRepository.save(comment);
        saveComment.addGroupInfo(saveComment.getId());

        return "ok";
    }

    @Transactional
    public String saveChildComment(CommentRequestDto commentDto, Long boardId, Long parentId, String token) {
        Board findBoard = boardRepository.findById(boardId).orElseThrow(() -> {
            throw new NotFoundBoardException("게시글을 찾을 수 없습니다.");
        });

        MemberInfoDto memberInfo = userServiceFeignClient.getMemberInfo(token);
        Comment comment = Comment.createComment()
                .content(commentDto.getContent())
                .nickname(memberInfo.getNickname())
                .board(findBoard)
                .build();
        comment.addGroupInfo(parentId);
        commentRepository.save(comment);

        return "ok";
    }

    public Page<Comment> getComments(Long boardId, Integer page) {
        return getPageComments(boardId, page);
    }



    private Page<Comment> getPageComments(Long boardId, Integer page) {
        PageRequest pageRequest = PageRequest.of(page - 1, 10, Sort.by(Sort.Direction.ASC, "id"));
        return commentRepository.findPageComments(boardId, pageRequest);
    }
}
