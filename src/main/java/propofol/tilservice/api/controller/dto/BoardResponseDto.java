package propofol.tilservice.api.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BoardResponseDto {
    private Long id;
    private String title;
    private String nickname;
    private String content;
    private String imageBytes;
    private String imageType;
    private Integer recommend;
    private Integer commentCount;
    private Boolean open;
    private LocalDateTime createdDate;
    private String createDate;
    private List<TagResponseDto> tagInfos = new ArrayList<>();
}