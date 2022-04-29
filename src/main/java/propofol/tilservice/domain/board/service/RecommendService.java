package propofol.tilservice.domain.board.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import propofol.tilservice.api.common.exception.SameMemberException;
import propofol.tilservice.domain.board.entity.Board;
import propofol.tilservice.domain.board.entity.Recommend;
import propofol.tilservice.domain.board.repository.BoardRepository;
import propofol.tilservice.domain.board.repository.RecommendRepository;
import propofol.tilservice.domain.exception.NotFoundBoardException;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecommendService {

    private final RecommendRepository recommendRepository;
    private final BoardRepository boardRepository;

    @Transactional
    public String createRecommend(String memberId, Long boardId){
        Board findBoard = boardRepository.findById(boardId).orElseThrow(() -> {
            throw new NotFoundBoardException("게시글을 찾을 수 없습니다.");
        });

        if(findBoard.getCreatedBy().equals(memberId)) throw new SameMemberException("같은 사용자입니다.");

        List<Recommend> recommends = findBoard.getRecommends();
        for (Recommend recommend : recommends) {
            if (recommend.getMemberId().equals(memberId)){
                findBoard.setDownRecommend();
                recommendRepository.delete(recommend);
                return "cancel";
            }
        }

        Recommend recommend = Recommend.createRecommend().memberId(memberId).build();
        findBoard.addRecommend(recommend);
        recommendRepository.save(recommend);
        findBoard.setUpRecommend();

        return "ok";
    }

    @Transactional
    public void bulkDelete(Long boardId){
        recommendRepository.bulkDeleteAll(boardId);
    }
}
