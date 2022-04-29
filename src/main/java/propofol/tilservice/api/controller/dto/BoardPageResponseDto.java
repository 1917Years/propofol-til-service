package propofol.tilservice.api.controller.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class BoardPageResponseDto {
    private Integer totalPageCount;
    private Long totalCount;
    private List<BoardResponseDto> boards = new ArrayList<>();
}
