package propofol.tilservice.api.controller.dto;

import lombok.Data;

@Data
public class ImageResponseDto {
    private byte[] image;
    private String imageType;
}
