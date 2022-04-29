package propofol.tilservice.domain.board.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import propofol.tilservice.api.common.exception.NotMatchMemberException;
import propofol.tilservice.domain.board.entity.Board;
import propofol.tilservice.domain.board.repository.BoardRepository;
import propofol.tilservice.domain.board.service.dto.BoardDto;
import propofol.tilservice.domain.exception.NotFoundBoardException;

@Slf4j
@Service
@RequiredArgsConstructor
public class BoardService {
    private final BoardRepository boardRepository;
    private final RecommendService recommendService;

    public Page<Board> getPageBoards(Integer pageNumber){
        PageRequest pageRequest =
                PageRequest.of(pageNumber - 1, 10, Sort.by(Sort.Direction.DESC, "id"));
        return boardRepository.findAll(pageRequest);
    }

    public Page<Board> getPagesByMemberId(Integer pageNumber, String memberId){
        PageRequest pageRequest =
                PageRequest.of(pageNumber - 1, 10, Sort.by(Sort.Direction.DESC, "id"));
        Page<Board> result = boardRepository.findPagesByCreatedBy(pageRequest, memberId);
        return result;
    }

    public String saveBoard(Board board){
        boardRepository.save(board);
        return "ok";
    }

    public Board getBoard(Long boardId){
        return boardRepository.findById(boardId).orElseThrow(() -> {
            throw new NotFoundBoardException("게시물을 찾을 수 없습니다.");
        });
    }

    public String deleteBoard(Long boardId, String memberId){
        Board findBoard = getBoard(boardId);
        recommendService.bulkDelete(boardId);
        if(findBoard.getCreatedBy().equals(memberId)) boardRepository.delete(findBoard);
        else throw new NotMatchMemberException("권한 없음.");
        return "ok";
    }

    public Board createBoard(BoardDto boardDto) {
        Board board = Board.createBoard()
                .title(boardDto.getTitle())
                .content(boardDto.getContent())
                .open(boardDto.getOpen())
                .recommend(0)
                .build();
        return board;
    }

    @Transactional
    public String updateBoard(Long boardId, BoardDto boardDto, String memberId) {
        Board findBoard = getBoard(boardId);
        if(findBoard.getCreatedBy().equals(memberId))
            findBoard.updateBoard(boardDto.getTitle(), boardDto.getContent(), boardDto.getOpen());
        else throw new NotMatchMemberException("권한 없음.");
        return "ok";
    }
}
