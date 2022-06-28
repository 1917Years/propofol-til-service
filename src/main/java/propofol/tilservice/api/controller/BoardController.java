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
import propofol.tilservice.api.common.exception.CompileException;
import propofol.tilservice.api.common.properties.FileProperties;
import propofol.tilservice.api.controller.dto.*;
import propofol.tilservice.api.feign.dto.TagDto;
import propofol.tilservice.api.feign.service.TagService;
import propofol.tilservice.api.feign.service.UserService;
import propofol.tilservice.api.service.*;
import propofol.tilservice.domain.board.entity.Board;
import propofol.tilservice.domain.board.entity.BoardTag;
import propofol.tilservice.domain.board.entity.Comment;
import propofol.tilservice.api.service.BoardService;
import propofol.tilservice.api.service.RecommendService;
import propofol.tilservice.domain.board.service.dto.BoardDto;
import propofol.tilservice.domain.file.entity.Image;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/boards")
public class BoardController {
    private final BoardService boardService;
    private final CodeService codeService;
    private final RecommendService recommendService;
    private final CommentService commentService;
    private final ImageService imageService;
    private final UserService userService;
    private final ModelMapper modelMapper;
    private final FileProperties fileProperties;
    private final TagService tagService;

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseDto CompileException(CompileException e){
        return new ResponseDto<>(HttpStatus.BAD_REQUEST.value(), "fail", "컴파일 실패", e.getMessage());
    }

    /**
     * 추천 수 상위 글 조회
     */
    @GetMapping("/portfolio/myBoards")
    @ResponseStatus(HttpStatus.OK)
    public List<BoardResponseDto> getTopRecommendBoard(@Token String memberId) {
        List<Board> boardByRecommend = boardService.findBoardByRecommend(memberId);
        if(boardByRecommend.size() == 0)
            return null;

        List<BoardResponseDto> responseDtos = new ArrayList<>();
        boardByRecommend.forEach(board -> {
            responseDtos.add(modelMapper.map(board, BoardResponseDto.class));
        });

        return responseDtos;
    }

    /**
     * 게시글 추천수 처리
     */
    @PostMapping("/{boardId}/recommend")
    @ResponseStatus(HttpStatus.OK)
    public ResponseDto createRecommend(@Token String memberId,
                                       @Jwt String token,
                                       @PathVariable(value = "boardId") Long boardId){

        return new ResponseDto<>(HttpStatus.OK.value(), "success",
                "추천 생성 성공!", recommendService.createRecommend(memberId, boardId, token));
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

        Comment comment = commentService.saveParentComment(requestDto, boardId, token, memberId);
        CommentResponseDto responseDto  = modelMapper.map(comment, CommentResponseDto.class);

        return new ResponseDto<>(HttpStatus.OK.value(), "success",
                "댓글 생성 성공!", responseDto);
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

        Comment comment = commentService.saveChildComment(requestDto, boardId, parentId, token, memberId);
        CommentResponseDto responseDto  = modelMapper.map(comment, CommentResponseDto.class);
        return new ResponseDto<>(HttpStatus.OK.value(), "success",
                "대댓글 생성 성공!", responseDto);
    }

    /**
     * 댓글 정보 제공
     */
    @GetMapping("/{boardId}/comments")
    @ResponseStatus(HttpStatus.OK)
    public ResponseDto getComments(@PathVariable(value = "boardId") Long boardId,
                                   @RequestParam("page") Integer page,
                                   @Jwt String token){
        Page<Comment> comments = commentService.getComments(boardId, page);

        return new ResponseDto<>(HttpStatus.OK.value(), "success",
                "댓글 조회 성공!", getCommentPageResponseDto(comments, boardId, token));
    }

    /**
     * 댓글 삭제
     */
    @DeleteMapping("/comments/{commentId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseDto deleteComments(@PathVariable("commentId") Long commentId){

        return new ResponseDto(HttpStatus.OK.value(), "success",
                "댓글 조회 성공!", commentService.deleteComment(commentId));
    }


    /**
     * 게시글 저장
     */
    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseDto createBoard(@RequestParam("title") String title,
                                   @RequestParam("content") String content,
                                   @RequestParam("open") Boolean open,
                                   @RequestParam(value = "tagId", required = false) List<Long> tagIds,
                                   @RequestParam(value = "fileName", required = false) List<String> fileNames,
                                   @Jwt String token) {
        BoardDto boardDto = createBoardDto(title, content, open);

        return new ResponseDto<>(HttpStatus.OK.value(), "success",
                "게시글 생성 성공!", boardService.saveBoard(boardDto, fileNames, tagIds, token));
    }

    /**
     * 이미지 저장
     */
    @PostMapping("/image")
    @ResponseStatus(HttpStatus.OK)
    public ResponseDto saveImage(@RequestParam(value = "file", required = false) List<MultipartFile> files,
                                 @RequestParam(value = "boardId", required = false) Long boardId) {
        List<String> storeImageNames = null;
        storeImageNames = imageService.getStoreImageNames(files, boardId, getBoardDir());

        return new ResponseDto<>(HttpStatus.OK.value(), "success",
                "이미지 생성 성공!", storeImageNames);
    }

    /**
     * 요청 페이지 번호에 맞는 데이터 제공
     */
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseDto getPageBoards(@RequestParam Integer page,
                                     @Token String memberId,
                                     @Jwt String token){
        BoardPageResponseDto boardListResponseDto
                = getBoardPageResponseDto(page, memberId, null, null, token);

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
    public ResponseDto updateBoard(@PathVariable Long boardId,
                                   @RequestParam("title") String title,
                                   @RequestParam("content") String content,
                                   @RequestParam("open") Boolean open,
                                   @RequestParam(value = "tagId", required = false) List<Long> tagIds,
                                   @RequestParam(value="fileName", required = false) List<String> fileNames,
                                   @Token String memberId,
                                   @Jwt String token){

        BoardDto boardDto = createBoardDto(title, content, open);
        boardService.updateBoard(boardId, boardDto, memberId, token, tagIds, fileNames);

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
    @GetMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    public ResponseDto findBoardBySearch(@RequestParam(value = "keyword", required = false) String keyword,
                                        @RequestParam(value = "tagId", required = false) Set<Long> tagIds,
                                        @RequestParam(value = "page") Integer page,
                                        @Token String memberId,
                                        @Jwt String token){
        return new ResponseDto<>(HttpStatus.OK.value(), "success",
                "게시글 제목 조회 성공!", getBoardPageResponseDto(page, memberId, keyword, tagIds, token));
    }

    /**
     * 코드 컴파일 추가
     */
    @PostMapping("/code/{boardId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseDto getCodeCompile(@Validated @RequestBody CodeRequestDto requestDto,
                                      @PathVariable("boardId") String boardId) throws IOException {
        return new ResponseDto(HttpStatus.OK.value(), "success", "컴파일 성공",
                codeService.compileCode(requestDto.getCode(), requestDto.getType().toLowerCase(Locale.ROOT),
                        boardId, getCodeDir()));
    }

    /**
     * 내 글 조회
     */
    @GetMapping("/myBoards")
    @ResponseStatus(HttpStatus.OK)
    public ResponseDto getPageBoardsByMemberId(@RequestParam Integer page,
                                               @Token String memberId,
                                               @Jwt String token){
        return new ResponseDto(HttpStatus.OK.value(), "success", "내 글 조회 성공",
                getBoardPageResponseDtoByMine(page, memberId, token));
    }

    private String getCodeDir() {
        return fileProperties.getCodeDir();
    }

    private String getBoardDir() {
        return fileProperties.getBoardDir();
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
        String userNickName = userService.getUserNickName(token, board.getCreatedBy());
        responseDto.setNickname(userNickName);

        List<String> images = responseDto.getImages();
        List<String> imageTypes = responseDto.getImageTypes();
        List<Image> findImages = imageService.getImagesByBoardId(board.getId());
        findImages.forEach(findImage -> {
            images.add(imageService.getImageBytes(findImage.getStoreFileName()));
            imageTypes.add(findImage.getContentType());
        });

        // 프로필
        ProfileImageResponseDto userProfile = userService.getUserProfile(token, userNickName);
        responseDto.setProfileBytes(userProfile.getProfileString());
        responseDto.setProfileType(userProfile.getProfileType());

        // 태그
        List<BoardTag> boardTags = board.getTags();

        Set<Long> tagIds = boardTags.stream().map(BoardTag::getTagId).collect(Collectors.toSet());
        List<TagDto> findTags = tagService.getTagsByTagIds(token, tagIds);

        findTags.forEach(findTag -> {
            responseDto.getTagInfos().add(modelMapper.map(findTag, TagResponseDto.class));
        });

        return responseDto;
    }

    /** 댓글 응답에 프로필 이미지 추가 */
    private CommentPageResponseDto getCommentPageResponseDto(Page<Comment> comments, Long boardId, String token) {
        CommentPageResponseDto commentPageResponseDto = new CommentPageResponseDto();
        commentPageResponseDto.setBoardId(boardId);
        commentPageResponseDto.setTotalCommentPageCount(comments.getTotalPages());
        commentPageResponseDto.setTotalCommentCount(comments.getTotalElements());
        comments.getContent().forEach(comment -> {
            CommentResponseDto responseDto = new CommentResponseDto(comment.getId(),
                    comment.getNickname(), comment.getContent(), comment.getGroupId(), comment.getCreatedDate());

            ProfileImageResponseDto userProfile = userService.getUserProfile(token, comment.getNickname());
            responseDto.setProfileBytes(userProfile.getProfileString());
            responseDto.setProfileType(userProfile.getProfileType());

            commentPageResponseDto.getComments().add(responseDto);
        });


        return commentPageResponseDto;
    }

    /** 닉네임 전송 추가 */
    private BoardPageResponseDto getBoardPageResponseDto(Integer page, String memberId, String keyword,
                                                         Set<Long> tagIds, String token) {
        Page<Board> pageBoards = null;
        if(keyword == null && tagIds == null) pageBoards = boardService.getPageBoardsNotMine(memberId, page);
        else if(keyword != null && tagIds == null) pageBoards = boardService.getPageByTitleKeywordNotMine(memberId, keyword, page);
        else if(keyword == null && tagIds != null) pageBoards = boardService.getPageByTagIdsNotMine(memberId, tagIds, page);
        else pageBoards = boardService.getPagesByKeywordAndTagIdsNotMine(page, memberId, keyword, tagIds);

        BoardPageResponseDto boardPageResponseDto = new BoardPageResponseDto();
        boardPageResponseDto.setTotalPageCount(pageBoards.getTotalPages());
        boardPageResponseDto.setTotalCount(pageBoards.getTotalElements());

        return createBoardPageResponseDto(token, pageBoards.getContent(), boardPageResponseDto);
    }

    private BoardPageResponseDto getBoardPageResponseDtoByMine(Integer page, String memberId, String token) {
        Page<Board> pageBoards = boardService.getPagesByMemberId(page, memberId);

        Set<Long> tagIds = new HashSet<>();
        pageBoards.getContent().forEach(board -> board.getTags().stream().map(tag -> tagIds.add(tag.getTagId())));

        BoardPageResponseDto boardPageResponseDto = new BoardPageResponseDto();
        boardPageResponseDto.setTotalPageCount(pageBoards.getTotalPages());
        boardPageResponseDto.setTotalCount(pageBoards.getTotalElements());
        BoardPageResponseDto boardListResponseDto = createBoardPageResponseDto(token,
                pageBoards.getContent(), boardPageResponseDto);

        return boardListResponseDto;
    }

    private BoardPageResponseDto createBoardPageResponseDto(String token, List<Board> boards,
                                                            BoardPageResponseDto boardPageResponseDto) {

        Set<Long> tagIds = new HashSet<>();
        boards.forEach(board -> {
            board.getTags().forEach(tag -> {
                tagIds.add(tag.getTagId());
            });
        });

        List<TagDto> tags = tagService.getTagsByTagIds(token, tagIds);

        boards.forEach(board -> {
            BoardResponseDto responseDto = modelMapper.map(board, BoardResponseDto.class);

            responseDto.setCommentCount(commentService.getCommentCount(responseDto.getId()));

            List<Image> images = board.getImages();
            if(images.size() > 0) {
                responseDto.setImageBytes(imageService.getTopImageBytes(images.get(0)));
                responseDto.setImageType(imageService.getImageType(images.get(0)));
            }
            responseDto.setNickname(userService.getUserNickName(token, board.getCreatedBy()));

            List<BoardTag> boardTags = board.getTags();
            boardTags.forEach(boardTag -> {
                tags.forEach(tag -> {
                    if(Objects.equals(tag.getId(), boardTag.getTagId())){
                        responseDto.getTagInfos().add(modelMapper.map(tag, TagResponseDto.class));
                    }
                });
            });

            boardPageResponseDto.getBoards().add(responseDto);
        });

        return boardPageResponseDto;
    }
}