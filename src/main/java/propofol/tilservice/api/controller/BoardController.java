package propofol.tilservice.api.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import propofol.tilservice.api.common.annotation.Jwt;
import propofol.tilservice.api.common.annotation.Token;
import propofol.tilservice.api.common.exception.BoardCreateException;
import propofol.tilservice.api.common.exception.BoardUpdateException;
import propofol.tilservice.api.common.exception.ImageCreateException;
import propofol.tilservice.api.common.properties.FileProperties;
import propofol.tilservice.api.controller.dto.*;
import propofol.tilservice.api.service.StreakService;
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
    private final StreakService streakService;

    /**
     * 게시글 추천수 처리
     */
    @PostMapping("/{boardId}/recommend")
    @ResponseStatus(HttpStatus.OK)
    public ResponseDto createRecommend(@Token String memberId,
                                  @PathVariable(value = "boardId") Long boardId){
        return new ResponseDto<>(HttpStatus.OK.value(), "success",
                "추천 생성 성공!", recommendService.createRecommend(memberId, boardId));
    }

    /**
     * 게시글 부모 댓글 생성
     */
    @PostMapping("/{boardId}/comment")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseDto createParentComment(@PathVariable(value = "boardId") Long boardId,
                                      @Validated @RequestBody CommentRequestDto requestDto,
                                      @Jwt String token) {
        return new ResponseDto<>(HttpStatus.OK.value(), "success",
                "댓글 생성 성공!", commentService.saveParentComment(requestDto, boardId, token));
    }

    /**
     * 게시글 자식 댓글 생성
     */
    @PostMapping("/{boardId}/{parentId}/comment")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseDto createChildComment(@PathVariable(value = "boardId") Long boardId,
                                @PathVariable(value="parentId") Long parentId,
                                @Validated @RequestBody CommentRequestDto requestDto,
                                     @Jwt String token) {
        return new ResponseDto<>(HttpStatus.OK.value(), "success",
                "대댓글 생성 성공!", commentService.saveChildComment(requestDto, boardId, parentId, token));
    }

    /**
     * 게시글 먼저 받고 이 후 Id로 댓글 받아가기
     * 댓글 정보 제공
     */
    @GetMapping("/{boardId}/comments")
    @ResponseStatus(HttpStatus.OK)
    public ResponseDto getComments(@PathVariable(value = "boardId") Long boardId,
                                              @RequestParam("page") Integer page){
         Page<Comment> comments = commentService.getComments(boardId, page);

        return new ResponseDto<>(HttpStatus.OK.value(), "success",
                "댓글 조회 성공!", getCommentPageResponseDto(comments, boardId));
    }

    /**
     * 게시글 저장
     */
    @Transactional
    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseDto createBoard(
            @RequestParam("title") String title,
            @RequestParam("content") String content,
            @RequestParam("open") Boolean open,
            @Jwt String token
    ) throws IOException {
        BoardDto boardDto = createBoardDto(title, content, open);

        try {
            streakService.saveStreak(token);

            Board board = boardService.createBoard(boardDto);
            boardService.saveBoard(board);

        }catch (Exception e){
            throw new BoardCreateException("게시글 생성 오류!");
        }

        return new ResponseDto<>(HttpStatus.OK.value(), "success",
                "게시글 생성 성공!", "ok");
    }

    @Transactional
    @PostMapping("/image")
    @ResponseStatus(HttpStatus.OK)
    public ResponseDto saveImage(@RequestParam("file") MultipartFile file){
        String path = "http://localhost:8000/til-service/api/v1/images";
        String saveFileName = null;
        try {
            saveFileName = fileService.saveImage(file);
        }catch (Exception e){
            throw new ImageCreateException("이미지 저장 오류");
        }
        path = path + "/" + saveFileName;
        return new ResponseDto(HttpStatus.OK.value(), "success",
                "이미지 저장 성공!", path);
    }

    /**
     * 요청 페이지 번호에 맞는 데이터 제공
     */
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseDto getPageBoards(@RequestParam Integer page){
        BoardPageResponseDto boardListResponseDto = new BoardPageResponseDto();
        Page<Board> pageBoards = boardService.getPageBoards(page);
        boardListResponseDto.setTotalPageCount(pageBoards.getTotalPages());
        boardListResponseDto.setTotalCount(pageBoards.getTotalElements());
        pageBoards.forEach(board -> {
            boardListResponseDto.getBoards().add(modelMapper.map(board, BoardResponseDto.class));
        });
        return new ResponseDto<>(HttpStatus.OK.value(), "success",
                "게시글 페이지 조회 성공!", boardListResponseDto);
    }

    /**
     * 게시글 정보 제공
     **/
    @GetMapping("/{boardId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseDto getBoardInfo(@PathVariable Long boardId){
        Board board = boardService.getBoard(boardId);
        return new ResponseDto<>(HttpStatus.OK.value(), "success",
                "게시글 조회 성공!", createBoardResponse(board));
    }

    /**
     * 게시글 수정
     */
    @PostMapping("/{boardId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseDto updateBoard(@PathVariable Long boardId, @RequestBody BoardUpdateRequestDto requestDto,
                              @Token String memberId, @Jwt String token){
        BoardDto boardDto = modelMapper.map(requestDto, BoardDto.class);

        try {
            streakService.saveStreak(token);
            boardService.updateBoard(boardId, boardDto, memberId);
        }catch (Exception e){
            throw new BoardUpdateException("게시물 수정 오류!");
        }

        return new ResponseDto<>(HttpStatus.OK.value(), "success",
                "게시글 수정 성공!", "ok");
    }

    /**
     * 게시글 삭제
     */
    @DeleteMapping("/{boardId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseDto deleteBoard(@PathVariable Long boardId, @Token String memberId){
        return new ResponseDto<>(HttpStatus.OK.value(), "success",
                "게시글 삭제 성공!", boardService.deleteBoard(boardId, memberId));
    }

    /**
     * 게시글 제목 검색
     */
    @GetMapping("/search/title/{keyword}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseDto findBoardByTitle(@PathVariable(value = "keyword") String keyword,
                                                 @RequestParam(value = "page") Integer page){
        Page<Board> boards = boardService.getPageByTitleKeyword(keyword, page);
        return new ResponseDto<>(HttpStatus.OK.value(), "success",
                "게시글 제목 조회 성공!", getBoardPageResponseDto(boards));
    }


    /**
     * 사용자 자신의 게시글 제공
     * 검색 기능으로 사용해도 됨
     */
    @GetMapping("/myBoards")
    @ResponseStatus(HttpStatus.OK)
    public ResponseDto getPageBoardsByMemberId(@RequestParam Integer page,
                                                        @Token String memberId){
        BoardPageResponseDto boardListResponseDto = new BoardPageResponseDto();
        Page<Board> pageBoards = boardService.getPagesByMemberId(page, memberId);
        boardListResponseDto.setTotalPageCount(pageBoards.getTotalPages());
        boardListResponseDto.setTotalCount(pageBoards.getTotalElements());
        pageBoards.forEach(board -> {
            boardListResponseDto.getBoards().add(modelMapper.map(board, BoardResponseDto.class));
        });
        return new ResponseDto<>(HttpStatus.OK.value(), "success",
                "게시글 조회 성공!", boardListResponseDto);
    }

    private BoardDto createBoardDto(String title, String content, Boolean open) {
        BoardDto boardDto = new BoardDto();
        boardDto.setTitle(title);
        boardDto.setContent(content);
        boardDto.setOpen(open);
        return boardDto;
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

    private BoardPageResponseDto getBoardPageResponseDto(Page<Board> boards) {
        BoardPageResponseDto boardPageResponseDto = new BoardPageResponseDto();
        boardPageResponseDto.setTotalPageCount(boards.getTotalPages());
        boardPageResponseDto.setTotalCount(boards.getTotalElements());
        List<BoardResponseDto> responseDtoBoards = boardPageResponseDto.getBoards();
        boards.getContent().forEach(board -> {
            responseDtoBoards.add(new BoardResponseDto(board.getId(), board.getTitle(), board.getContent(),
                    board.getRecommend(), board.getOpen()));
        });
        return boardPageResponseDto;
    }

}
