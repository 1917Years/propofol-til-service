package propofol.tilservice.domain.board.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import propofol.tilservice.api.common.exception.NotMatchMemberException;
import propofol.tilservice.api.service.ImageService;
import propofol.tilservice.api.service.StreakService;
import propofol.tilservice.domain.board.entity.Board;
import propofol.tilservice.domain.board.repository.BoardRepository;
import propofol.tilservice.domain.board.repository.CommentRepository;
import propofol.tilservice.domain.board.repository.RecommendRepository;
import propofol.tilservice.domain.board.service.dto.BoardDto;
import propofol.tilservice.domain.exception.NotFoundBoardException;
import propofol.tilservice.domain.file.entity.Image;

import java.io.File;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BoardService {
    private final BoardRepository boardRepository;
    private final CommentRepository commentRepository;
    private final RecommendRepository recommendRepository;
    private final ImageService imageService;
    private final StreakService streakService;

    /**
     *  게시글 전체 페이지 조회
     */
    public Page<Board> getPageBoards(Integer pageNumber){
        PageRequest pageRequest =
                PageRequest.of(pageNumber - 1, 10, Sort.by(Sort.Direction.DESC, "id"));
        return boardRepository.findAll(pageRequest);
    }

    /**
     * 제목 검색
     */
    public Page<Board> getPageByTitleKeyword(String keyword, int page){
        PageRequest pageRequest = PageRequest.of(page - 1, 10, Sort.by(Sort.Direction.DESC, "id"));
        return boardRepository.findPageByTitleKeyword(keyword, pageRequest);
    }

    /**
     * 회원 Id를 사용한 게시글 페이지 조회
     */
    public Page<Board> getPagesByMemberId(Integer pageNumber, String memberId){
        PageRequest pageRequest =
                PageRequest.of(pageNumber - 1, 10, Sort.by(Sort.Direction.DESC, "id"));
        Page<Board> result = boardRepository.findPagesByCreatedBy(pageRequest, memberId);
        return result;
    }

    /**
     * 게시글 저장
     */
    @Transactional
    public Board saveBoard(Board board){
        return boardRepository.save(board);
    }

    /**
     * 게시글 아이디 조회
     */
    public Board getBoard(Long boardId){
        return boardRepository.findById(boardId).orElseThrow(() -> {
            throw new NotFoundBoardException("게시물을 찾을 수 없습니다.");
        });
    }

    /**
     * 게시글 삭제
     */
    @Transactional
    public String deleteBoard(Long boardId, String memberId){
        Board findBoard = getBoard(boardId);

        if(!findBoard.getCreatedBy().equals(memberId)) throw new NotMatchMemberException("권한 없음.");

        List<Image> findImages = imageService.getImagesByBoardId(boardId);
        if(findImages != null){
            findImages.forEach(image -> {
                File savedFile =
                        new File(imageService.findBoardPath(
                                imageService.getUploadDir()) + "/" + image.getStoreFileName()
                        );

                if(savedFile.exists()) savedFile.delete();
            });

            imageService.deleteImages(boardId);
        }
        recommendRepository.deleteBulkRecommends(boardId); // 추천 삭제
        commentRepository.deleteBulkComments(boardId); // 댓글 삭제
        boardRepository.delete(findBoard); // 게시글 삭제

        return "ok";
    }

    /**
     * Board Entity 생성
     */
    public Board createBoard(BoardDto boardDto) {
        Board board = Board.createBoard()
                .title(boardDto.getTitle())
                .content(boardDto.getContent())
                .open(boardDto.getOpen())
                .recommend(0)
                .build();
        return board;
    }

    /**
     * 게시글 수정
     */
    @Transactional
    public Board updateBoard(Long boardId, BoardDto boardDto, String memberId, String token, List<String> fileNames) {
        Board findBoard = getBoard(boardId);
        if(findBoard.getCreatedBy().equals(memberId))
            findBoard.updateBoard(boardDto.getTitle(), boardDto.getContent(), boardDto.getOpen());
        else throw new NotMatchMemberException("권한 없음.");

        if(fileNames != null){
            fileNames.forEach(fileName -> {
                imageService.changeImageBoardId(fileNames, findBoard);
            });
        }

        streakService.saveStreak(token);
        return findBoard;
    }
}