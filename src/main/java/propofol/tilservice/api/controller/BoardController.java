package propofol.tilservice.api.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import propofol.tilservice.api.common.annotation.Token;
import propofol.tilservice.api.common.properties.FileProperties;
import propofol.tilservice.api.controller.dto.BoardListResponseDto;
import propofol.tilservice.api.controller.dto.BoardCreateRequestDto;
import propofol.tilservice.api.controller.dto.BoardResponseDto;
import propofol.tilservice.api.controller.dto.BoardUpdateRequestDto;
import propofol.tilservice.domain.board.entity.Board;
import propofol.tilservice.domain.board.service.BoardService;
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
    private final ModelMapper modelMapper;
    private final FileProperties fileProperties;
    private final ImageService fileService;

    @GetMapping
    public BoardListResponseDto getPageBoards(@RequestParam Integer page){
        BoardListResponseDto boardListResponseDto = new BoardListResponseDto();
        Page<Board> pageBoards = boardService.getPageBoards(page);
        boardListResponseDto.setTotalPageCount(pageBoards.getTotalPages());
        boardListResponseDto.setTotalCount(pageBoards.getTotalElements());
        pageBoards.forEach(board -> {
            boardListResponseDto.getBoards().add(modelMapper.map(board, BoardResponseDto.class));
        });
        return boardListResponseDto;
    }

    @GetMapping("/myBoards")
    public BoardListResponseDto getPageBoardsByMemberId(@RequestParam Integer page,
                                                        @Token String memberId){
        BoardListResponseDto boardListResponseDto = new BoardListResponseDto();
        Page<Board> pageBoards = boardService.getPagesByMemberId(page, memberId);
        boardListResponseDto.setTotalPageCount(pageBoards.getTotalPages());
        boardListResponseDto.setTotalCount(pageBoards.getTotalElements());
        pageBoards.forEach(board -> {
            boardListResponseDto.getBoards().add(modelMapper.map(board, BoardResponseDto.class));
        });
        return boardListResponseDto;
    }

    @PostMapping("/recommend/{boardId}")
    public String createRecommend(@Token String memberId,
                                  @PathVariable(value = "boardId") Long boardId){
        return boardService.createRecommend(memberId, boardId);
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

    @PostMapping("/{boardId}")
    public String updateBoard(@PathVariable Long boardId, @RequestBody BoardUpdateRequestDto requestDto,
                              @Token String memberId){
        BoardDto boardDto = modelMapper.map(requestDto, BoardDto.class);
        return boardService.updateBoard(boardId, boardDto, memberId);
    }

    @GetMapping("/{boardId}")
    public BoardResponseDto getBoardInfo(@PathVariable Long boardId){
        Board board = boardService.getBoard(boardId);
        return createBoardResponse(board);
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

    @DeleteMapping("/{boardId}")
    public String deleteBoard(@PathVariable Long boardId, @Token String memberId){
        return boardService.deleteBoard(boardId, memberId);
    }
}
