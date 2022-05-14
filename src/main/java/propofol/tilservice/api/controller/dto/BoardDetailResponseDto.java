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
public class BoardDetailResponseDto {
    private String title;
    private String content;
    private String nickname;
    private List<byte[]> images = new ArrayList<>();
    private List<String> imageTypes = new ArrayList<>();
    private Boolean open;
    private int recommend;
    private int commentCount;
    private LocalDateTime createdDate;
}
