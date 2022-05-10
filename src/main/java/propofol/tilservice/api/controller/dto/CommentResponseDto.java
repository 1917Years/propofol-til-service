package propofol.tilservice.api.controller.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CommentResponseDto {
    private Long id;
    private String nickname;
    private String content;
    private Long groupId;

    public CommentResponseDto(Long id, String nickname, String content, Long groupId) {
        this.id = id;
        this.nickname = nickname;
        this.content = content;
        this.groupId = groupId;
    }
}
