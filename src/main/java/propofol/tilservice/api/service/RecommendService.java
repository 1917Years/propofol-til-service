package propofol.tilservice.api.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import propofol.tilservice.api.common.exception.SameMemberException;
import propofol.tilservice.api.feign.service.AlarmService;
import propofol.tilservice.api.feign.service.UserService;
import propofol.tilservice.domain.board.entity.Board;
import propofol.tilservice.domain.board.entity.Recommend;
import propofol.tilservice.domain.board.repository.BoardRepository;
import propofol.tilservice.domain.board.repository.RecommendRepository;
import propofol.tilservice.domain.exception.NotFoundBoardException;

import java.util.List;

import static propofol.tilservice.api.feign.AlarmType.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecommendService {

    private final RecommendRepository recommendRepository;
    private final BoardRepository boardRepository;
    private final AlarmService alarmService;
    private final UserService userService;

    @Transactional
    public String createRecommend(String memberId, Long boardId, String token){
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
        userService.plusMemberTotalRecommend(token, Long.parseLong(findBoard.getCreatedBy()));
        findBoard.setUpRecommend();

        String userNickName = userService.getUserNickName(token, memberId);

        alarmService.saveAlarm(Long.parseLong(findBoard.getCreatedBy()),
                "등록된 게시글 " + findBoard.getTitle() + "에 " + userNickName + "님이 좋아요를 누르셨습니다.", token, LIKE, boardId);

        return "ok";
    }

}
