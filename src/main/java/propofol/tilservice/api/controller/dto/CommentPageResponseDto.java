package propofol.tilservice.api.controller.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class CommentPageResponseDto {
    private Long boardId;
    private Integer totalCommentPageCount;
    private Long totalCommentCount;
    private List<CommentResponseDto> comments = new ArrayList<>();
}
