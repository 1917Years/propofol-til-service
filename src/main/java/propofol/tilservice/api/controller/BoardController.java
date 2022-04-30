package propofol.tilservice.api.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import propofol.tilservice.api.common.annotation.Jwt;
import propofol.tilservice.api.common.annotation.Token;
import propofol.tilservice.api.common.properties.FileProperties;
import propofol.tilservice.api.controller.dto.*;
import propofol.tilservice.domain.board.entity.Board;
import propofol.tilservice.domain.board.entity.Comment;
import propofol.tilservice.domain.board.service.BoardService;
import propofol.tilservice.api.service.CommentService;
import propofol.tilservice.domain.board.service.RecommendService;
import propofol.tilservice.domain.board.service.dto.BoardDto;
import propofol.tilservice.domain.file.service.ImageService;

import java.io.IOException;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/boards")
public class BoardController {
    private final BoardService boardService;
    private final RecommendService recommendService;
    private final ModelMapper modelMapper;
    private final FileProperties fileProperties;
    private final ImageService fileService;
    private final CommentService commentService;

    /**
     * 게시글 추천수 처리
     */
    @PostMapping("/{boardId}/recommend")
    public String createRecommend(@Token String memberId,
                                  @PathVariable(value = "boardId") Long boardId){
        return recommendService.createRecommend(memberId, boardId);
    }

    /**
     * 게시글 부모 댓글 생성
     */
    @PostMapping("/{boardId}/comment")
    public String createParentComment(@PathVariable(value = "boardId") Long boardId,
                                      @Validated @RequestBody CommentRequestDto requestDto,
                                      @Jwt String token) {
        return commentService.saveParentComment(requestDto, boardId, token);
    }

    /**
     * 게시글 자식 댓글 생성
     */
    @PostMapping("/{boardId}/{parentId}/comment")
    public String createChildComment(@PathVariable(value = "boardId") Long boardId,
                                @PathVariable(value="parentId") Long parentId,
                                @Validated @RequestBody CommentRequestDto requestDto,
                                     @Jwt String token) {
        return commentService.saveChildComment(requestDto, boardId, parentId, token);
    }

    /**
     * 댓글 정보 제공
     */
    @GetMapping("/{boardId}/comments")
    public CommentPageResponseDto getComments(@PathVariable(value = "boardId") Long boardId,
                                              @RequestParam("page") Integer page){
        Page<Comment> comments = commentService.getComments(boardId, page);

        return getCommentPageResponseDto(comments, boardId);
    }

    /**
     * 파일 없이 게시글 저장
     */
    @PostMapping
    public String createBoard(@Validated @RequestBody BoardCreateRequestDto requestDto){
        BoardDto boardDto = modelMapper.map(requestDto, BoardDto.class);

        boardService.saveBoard(boardService.createBoard(boardDto));
        return "ok";
    }

    /**
     * 파일과 함께 게시글 저장
     */
    @PostMapping("/files")
    @Transactional
    public String createBoardWithFiles(
            @RequestParam("file") List<MultipartFile> files,
            @RequestParam("title") String title,
            @RequestParam("content") String content,
            @RequestParam("open") Boolean open
    ) throws IOException {

        BoardDto boardDto = new BoardDto();
        boardDto.setTitle(title);
        boardDto.setContent(content);
        boardDto.setOpen(open);

        Board board = boardService.createBoard(boardDto);
        boardService.saveBoard(board);

        fileService.saveBoardFile(fileProperties.getBoardDir(), files, board);

        return "ok";
    }

    /**
     * 요청 페이지 번호에 맞는 데이터 제공
     */
    @GetMapping
    public BoardPageResponseDto getPageBoards(@RequestParam Integer page){
        BoardPageResponseDto boardListResponseDto = new BoardPageResponseDto();
        Page<Board> pageBoards = boardService.getPageBoards(page);
        boardListResponseDto.setTotalPageCount(pageBoards.getTotalPages());
        boardListResponseDto.setTotalCount(pageBoards.getTotalElements());
        pageBoards.forEach(board -> {
            boardListResponseDto.getBoards().add(modelMapper.map(board, BoardResponseDto.class));
        });
        return boardListResponseDto;
    }

    /**
     * 사용자 자신의 게시글 제공
     */
    @GetMapping("/myBoards")
    public BoardPageResponseDto getPageBoardsByMemberId(@RequestParam Integer page,
                                                        @Token String memberId){
        BoardPageResponseDto boardListResponseDto = new BoardPageResponseDto();
        Page<Board> pageBoards = boardService.getPagesByMemberId(page, memberId);
        boardListResponseDto.setTotalPageCount(pageBoards.getTotalPages());
        boardListResponseDto.setTotalCount(pageBoards.getTotalElements());
        pageBoards.forEach(board -> {
            boardListResponseDto.getBoards().add(modelMapper.map(board, BoardResponseDto.class));
        });
        return boardListResponseDto;
    }

    /**
     * 게시글 정보 제공
     **/
    @GetMapping("/{boardId}")
    public BoardResponseDto getBoardInfo(@PathVariable Long boardId){
        Board board = boardService.getBoard(boardId);
        return createBoardResponse(board);
    }

    /**
     * 게시글 수정
     */
    @PostMapping("/{boardId}")
    public String updateBoard(@PathVariable Long boardId, @RequestBody BoardUpdateRequestDto requestDto,
                              @Token String memberId){
        BoardDto boardDto = modelMapper.map(requestDto, BoardDto.class);
        return boardService.updateBoard(boardId, boardDto, memberId);
    }

    /**
     * 게시글 삭제
     */
    @DeleteMapping("/{boardId}")
    public String deleteBoard(@PathVariable Long boardId, @Token String memberId){
        return boardService.deleteBoard(boardId, memberId);
    }

    private BoardResponseDto createBoardResponse(Board board) {
        BoardResponseDto boardResponseDto = new BoardResponseDto();
        boardResponseDto.setId(board.getId());
        boardResponseDto.setTitle(board.getTitle());
        boardResponseDto.setContent(board.getContent());
        boardResponseDto.setOpen(board.getOpen());
        boardResponseDto.setRecommend(board.getRecommend());
        return boardResponseDto;
    }

    private CommentPageResponseDto getCommentPageResponseDto(Page<Comment> comments, Long boardId) {
        CommentPageResponseDto commentPageResponseDto = new CommentPageResponseDto();
        commentPageResponseDto.setBoardId(boardId);
        commentPageResponseDto.setTotalCommentPageCount(comments.getTotalPages());
        commentPageResponseDto.setTotalCommentCount(comments.getTotalElements());
        comments.getContent().forEach(comment -> {
            commentPageResponseDto.getComments().add(new CommentResponseDto(comment.getId(),
                            comment.getNickname(), comment.getContent(), comment.getGroupId()));
        });

        return commentPageResponseDto;
    }

}
