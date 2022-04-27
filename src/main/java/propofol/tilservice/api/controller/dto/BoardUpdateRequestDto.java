package propofol.tilservice.api.controller.dto;

import lombok.Data;

@Data
public class BoardUpdateRequestDto {
    private String title;
    private String content;
    private Boolean open;
}
