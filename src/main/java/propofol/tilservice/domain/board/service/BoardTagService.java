package propofol.tilservice.domain.board.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import propofol.tilservice.domain.board.entity.BoardTag;
import propofol.tilservice.domain.board.repository.BoardTagRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BoardTagService {

    private final BoardTagRepository boardTagRepository;

    public void saveAllTags(List<BoardTag> tags){
        boardTagRepository.saveAll(tags);
    }

    public void deleteTagsByBoardId(Long boardId) {
        boardTagRepository.deleteAllByBoardId(boardId);
    }
}
