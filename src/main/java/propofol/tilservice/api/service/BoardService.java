package propofol.tilservice.api.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import propofol.tilservice.api.common.exception.NotMatchMemberException;
import propofol.tilservice.api.feign.AlarmType;
import propofol.tilservice.api.feign.dto.MemberSaveBoardDto;
import propofol.tilservice.api.feign.service.AlarmService;
import propofol.tilservice.api.feign.service.UserService;
import propofol.tilservice.domain.board.entity.Board;
import propofol.tilservice.domain.board.entity.BoardTag;
import propofol.tilservice.domain.board.repository.BoardRepository;
import propofol.tilservice.domain.board.repository.CommentRepository;
import propofol.tilservice.domain.board.repository.RecommendRepository;
import propofol.tilservice.domain.board.service.BoardTagService;
import propofol.tilservice.domain.board.service.dto.BoardDto;
import propofol.tilservice.domain.exception.NotFoundBoardException;
import propofol.tilservice.domain.file.entity.Image;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class BoardService {
    private final BoardRepository boardRepository;
    private final CommentRepository commentRepository;
    private final RecommendRepository recommendRepository;
    private final ImageService imageService;
    private final StreakService streakService;
    private final BoardTagService boardTagService;
    private final UserService userService;
    private final AlarmService alarmService;


    /**
     *  게시글 전체 페이지 조회, 자신의 것 제외
     */
    public Page<Board> getPageBoardsNotMine(String memberId, Integer pageNumber){
        PageRequest pageRequest =
                PageRequest.of(pageNumber - 1, 10, Sort.by(Sort.Direction.DESC, "id"));
        return boardRepository.findAllNotMine(memberId, pageRequest);
    }

    /**
     * 제목 검색
     */
    public Page<Board> getPageByTitleKeywordNotMine(String memberId, String keyword, int page){
        PageRequest pageRequest = PageRequest.of(page - 1, 10, Sort.by(Sort.Direction.DESC, "id"));
        return boardRepository.findPageByTitleKeywordNotMine(memberId, keyword, pageRequest);
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
    public String saveBoard(BoardDto boardDto, List<String> fileNames, List<Long> tagIds, String token){
        Board board = createBoard(boardDto);

        Board saveBoard = boardRepository.save(board);

        if(fileNames != null){
            imageService.changeImageBoardId(fileNames, saveBoard);
        }

        if(tagIds != null){
            List<BoardTag> boardTags = createBoardTags(tagIds, saveBoard);

            boardTagService.saveAllTags(boardTags);
            userService.saveMemberTag(token, tagIds);
        }

        streakService.saveStreak(token);

        MemberSaveBoardDto memberSaveBoardDto
                = userService.getMyFollowerIdsAndNickname(token, Long.parseLong(saveBoard.getCreatedBy()));

        if(memberSaveBoardDto.getMemberIds().size() > 0) {
            alarmService.saveListAlarm(new ArrayList<>(memberSaveBoardDto.getMemberIds()),
                    memberSaveBoardDto.getNickname() + "님이 새로운 게시글을 작성하였습니다.", AlarmType.SUBSCRIBER_BOARD.toString(),
                    saveBoard.getId(), token);
        }

        return "ok";
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
     * 게시글 수정
     */
    @Transactional
    public Board updateBoard(Long boardId, BoardDto boardDto, String memberId, String token,
                             List<Long> tagIds, List<String> fileNames) {
        Board findBoard = getBoard(boardId);
        if(findBoard.getCreatedBy().equals(memberId))
            findBoard.updateBoard(boardDto.getTitle(), boardDto.getContent(), boardDto.getOpen());
        else throw new NotMatchMemberException("권한 없음.");

        if(fileNames != null){
            fileNames.forEach(fileName -> {
                imageService.changeImageBoardId(fileNames, findBoard);
            });
        }

        if(tagIds != null){
            boardTagService.deleteTagsByBoardId(boardId);

            List<BoardTag> boardTags = createBoardTags(tagIds, findBoard);

            boardTagService.saveAllTags(boardTags);
            userService.saveMemberTag(token, tagIds);
        }

        streakService.saveStreak(token);
        return findBoard;
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

    public Page<Board> getPageByTagIdsNotMine(String memberId, Set<Long> tagIds, int page){
        PageRequest pageRequest = PageRequest.of(page - 1, 10);
        return boardRepository.findPageByTagIdsNotMine(memberId, tagIds, pageRequest);
    }

    public Page<Board> getPagesByKeywordAndTagIdsNotMine(Integer pageNumber, String memberId,
                                                            String keyword, Set<Long> tagIds){
        PageRequest pageRequest = PageRequest.of(pageNumber - 1, 10);
        return boardRepository.findPagesByKeywordAndTagIdsNotMine(pageRequest, memberId, keyword, tagIds);
    }

    private List<BoardTag> createBoardTags(List<Long> tagIds, Board saveBoard) {
        List<BoardTag> boardTags = new ArrayList<>();
        tagIds.forEach(id -> {
            BoardTag tag = BoardTag.createTag().tagId(id).build();
            tag.changeBoard(saveBoard);
            boardTags.add(tag);
        });
        return boardTags;
    }
}