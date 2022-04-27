package propofol.tilservice.domain.board.service.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class BoardDto {
    private String title;
    private String content;
    private Boolean open;
}
