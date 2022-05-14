package propofol.tilservice.api.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import propofol.tilservice.api.common.annotation.Jwt;
import propofol.tilservice.api.common.annotation.Token;
import propofol.tilservice.api.common.exception.BoardCreateException;
import propofol.tilservice.api.common.exception.BoardUpdateException;
import propofol.tilservice.api.common.exception.ImageCreateException;
import propofol.tilservice.api.controller.dto.*;
import propofol.tilservice.api.service.StreakService;
import propofol.tilservice.api.service.UserService;
import propofol.tilservice.domain.board.entity.Board;
import propofol.tilservice.domain.board.entity.Comment;
import propofol.tilservice.domain.board.service.BoardService;
import propofol.tilservice.api.service.CommentService;
import propofol.tilservice.domain.board.service.RecommendService;
import propofol.tilservice.domain.board.service.dto.BoardDto;
import propofol.tilservice.api.service.ImageService;
import propofol.tilservice.domain.file.entity.Image;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/boards")
public class BoardController {
    private final BoardService boardService;
    private final RecommendService recommendService;
    private final CommentService commentService;
    private final StreakService streakService;
    private final ImageService imageService;
    private final UserService userService;
    private final ModelMapper modelMapper;

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
                                      @Token String memberId,
                                      @Jwt String token) {
        return new ResponseDto<>(HttpStatus.OK.value(), "success",
                "댓글 생성 성공!", commentService.saveParentComment(requestDto, boardId, token, memberId));
    }

    /**
     * 게시글 자식 댓글 생성
     */
    @PostMapping("/{boardId}/{parentId}/comment")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseDto createChildComment(@PathVariable(value = "boardId") Long boardId,
                                @PathVariable(value="parentId") Long parentId,
                                @Validated @RequestBody CommentRequestDto requestDto,
                                          @Token String memberId,
                                          @Jwt String token) {
        return new ResponseDto<>(HttpStatus.OK.value(), "success",
                "대댓글 생성 성공!", commentService.saveChildComment(requestDto, boardId, parentId, token, memberId));
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
    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseDto createBoard(@RequestParam("title") String title,
                                   @RequestParam("content") String content,
                                   @RequestParam("open") Boolean open,
                                   @RequestParam(value = "fileName", required = false) List<String> fileNames,
                                   @Jwt String token) {
        BoardDto boardDto = createBoardDto(title, content, open);

        try {
            Board board = boardService.createBoard(boardDto);
            Board saveBoard = boardService.saveBoard(board);

            if(fileNames != null){
                imageService.changeImageBoardId(fileNames, saveBoard);
            }

            streakService.saveStreak(token);

        }catch (Exception e){
            throw new BoardCreateException("게시글 생성 오류!");
        }

        return new ResponseDto<>(HttpStatus.OK.value(), "success",
                "게시글 생성 성공!", "ok");
    }

    @PostMapping("/image")
    @ResponseStatus(HttpStatus.OK)
    public ResponseDto saveImage(@RequestParam(value = "file", required = false) List<MultipartFile> files) {
        List<String> storeImageNames = null;

        try {
            if(files != null){
                storeImageNames = imageService.getStoreImageNames(files);
            }
        }catch (Exception e){
            throw new ImageCreateException("이미지 생성 오류");
        }

        return new ResponseDto<>(HttpStatus.OK.value(), "success",
                "이미지 생성 성공!", storeImageNames);
    }

    /**
     * 요청 페이지 번호에 맞는 데이터 제공
     */
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseDto getPageBoards(@RequestParam Integer page){
        BoardPageResponseDto boardListResponseDto = getBoardPageResponseDto(page, null, null);
        return new ResponseDto<>(HttpStatus.OK.value(), "success",
                "게시글 페이지 조회 성공!", boardListResponseDto);
    }

    /**
     * 게시글 정보 제공
     **/
    @GetMapping("/{boardId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseDto getBoardInfo(@PathVariable Long boardId,
                                    @Jwt String token){
        return new ResponseDto<>(HttpStatus.OK.value(), "success",
                "게시글 조회 성공!", createBoardDetailResponse(token, boardService.getBoard(boardId)));
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
        return new ResponseDto<>(HttpStatus.OK.value(), "success",
                "게시글 제목 조회 성공!", getBoardPageResponseDto(page, null, keyword));
    }


    /**
     * 사용자 자신의 게시글 제공
     * 검색 기능으로 사용해도 됨
     */
    @GetMapping("/myBoards")
    @ResponseStatus(HttpStatus.OK)
    public BoardPageResponseDto getPageBoardsByMemberId(@RequestParam Integer page,
                                                        @Token String memberId){
        return getBoardPageResponseDto(page, memberId, null);
    }

    private BoardDto createBoardDto(String title, String content, Boolean open) {
        BoardDto boardDto = new BoardDto();
        boardDto.setTitle(title);
        boardDto.setContent(content);
        boardDto.setOpen(open);
        return boardDto;
    }


    private BoardDetailResponseDto createBoardDetailResponse(String token, Board board) {
        BoardDetailResponseDto responseDto = modelMapper.map(board, BoardDetailResponseDto.class);
        responseDto.setCommentCount(commentService.getCommentCount(board.getId()));
        responseDto.setNickname(userService.getUserNickName(token, board.getCreatedBy()));

        List<byte[]> images = responseDto.getImages();
        List<String> imageTypes = responseDto.getImageTypes();
        List<Image> findImages = imageService.getImagesByBoardId(board.getId());
        findImages.forEach(findImage -> {
            images.add(imageService.getImageBytes(findImage.getStoreFileName()));
            imageTypes.add(findImage.getContentType());
        });
        return responseDto;
    }

    private CommentPageResponseDto getCommentPageResponseDto(Page<Comment> comments, Long boardId) {
        CommentPageResponseDto commentPageResponseDto = new CommentPageResponseDto();
        commentPageResponseDto.setBoardId(boardId);
        commentPageResponseDto.setTotalCommentPageCount(comments.getTotalPages());
        commentPageResponseDto.setTotalCommentCount(comments.getTotalElements());
        comments.getContent().forEach(comment -> {
            commentPageResponseDto.getComments().add(new CommentResponseDto(comment.getId(),
                            comment.getNickname(), comment.getContent(), comment.getGroupId(), comment.getCreatedDate()));
        });

        return commentPageResponseDto;
    }

    private BoardPageResponseDto getBoardPageResponseDto(Integer page, String memberId, String keyword) {
        BoardPageResponseDto boardListResponseDto = new BoardPageResponseDto();
        Page<Board> pageBoards = null;

        if(memberId == null && keyword == null) pageBoards = boardService.getPageBoards(page);
        else if(memberId == null && keyword != null) pageBoards = boardService.getPageByTitleKeyword(keyword, page);
        else pageBoards = boardService.getPagesByMemberId(page, memberId);

        boardListResponseDto.setTotalPageCount(pageBoards.getTotalPages());
        boardListResponseDto.setTotalCount(pageBoards.getTotalElements());
        pageBoards.forEach(board -> {
            BoardResponseDto responseDto = modelMapper.map(board, BoardResponseDto.class);
            responseDto.setCommentCount(commentService.getCommentCount(responseDto.getId()));
            Image findImage = imageService.getTopImage(board.getId());
            responseDto.setImageBytes(imageService.getTopImageBytes(findImage));
            responseDto.setImageType(imageService.getImageType(findImage));
            boardListResponseDto.getBoards().add(responseDto);
        });
        return boardListResponseDto;
    }
}
