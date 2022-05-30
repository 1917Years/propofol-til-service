package propofol.tilservice.api.controller.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class CommentResponseDto {
    private Long id;
    private String nickname;
    private String content;
    private Long groupId;
    private LocalDateTime createdDate;
    private String profileBytes;
    private String profileType;

    public CommentResponseDto(Long id, String nickname, String content, Long groupId, LocalDateTime createdDate) {
        this.id = id;
        this.nickname = nickname;
        this.content = content;
        this.groupId = groupId;
        this.createdDate = createdDate;
    }
}