package propofol.tilservice.api.controller.dto;

import lombok.Data;

@Data
public class ResponseImageDto {
    private byte[] image;
    private String imageType;
}
