package propofol.tilservice.domain.board.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import propofol.tilservice.api.controller.dto.BoardUpdateRequestDto;
import propofol.tilservice.domain.board.entity.Board;
import propofol.tilservice.domain.board.repository.BoardRepository;
import propofol.tilservice.domain.board.service.dto.BoardDto;
import propofol.tilservice.domain.exception.NotFoundBoard;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BoardService {
    private final BoardRepository boardRepository;

    public Page<Board> getPageBoards(Integer pageNumber){
        PageRequest pageRequest =
                PageRequest.of(pageNumber - 1, 10, Sort.by(Sort.Direction.DESC, "id"));
        return boardRepository.findAll(pageRequest);
    }

    public String saveBoard(BoardDto boardDto){
        Board board = createBoard(boardDto);
        boardRepository.save(board);
        return "ok";
    }

    public Board getBoard(Long boardId){
        return boardRepository.findById(boardId).orElseThrow(() -> {
            throw new NotFoundBoard("게시물을 찾을 수 없습니다.");
        });
    }

    public String deleteBoard(Long boardId){
        Board board = getBoard(boardId);
        boardRepository.delete(board);
        return "ok";
    }

    private Board createBoard(BoardDto boardDto) {
        Board board = Board.createBoard()
                .title(boardDto.getTitle())
                .content(boardDto.getContent())
                .open(boardDto.getOpen())
                .recommend(0)
                .build();
        return board;
    }

    @Transactional
    public String updateBoard(Long boardId, BoardDto boardDto) {
        Board findBoard = getBoard(boardId);
        findBoard.updateBoard(boardDto.getTitle(), boardDto.getContent(), boardDto.getOpen());
        return "ok";
    }
}
