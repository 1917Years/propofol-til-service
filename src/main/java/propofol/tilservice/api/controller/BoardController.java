package propofol.tilservice.api.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import propofol.tilservice.api.common.annotation.Token;
import propofol.tilservice.api.controller.dto.BoardListResponseDto;
import propofol.tilservice.api.controller.dto.BoardCreateRequestDto;
import propofol.tilservice.api.controller.dto.BoardResponseDto;
import propofol.tilservice.api.controller.dto.BoardUpdateRequestDto;
import propofol.tilservice.domain.board.entity.Board;
import propofol.tilservice.domain.board.service.BoardService;
import propofol.tilservice.domain.board.service.dto.BoardDto;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/boards")
public class BoardController {
    private final BoardService boardService;
    private final ModelMapper modelMapper;

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

    @PostMapping
    public String createBoard(@Validated @RequestBody BoardCreateRequestDto requestDto){
        BoardDto boardDto = modelMapper.map(requestDto, BoardDto.class);
        return boardService.saveBoard(boardDto);
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
