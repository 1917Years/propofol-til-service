package propofol.tilservice.api.controller.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ImagesResponseDto {
    private List<byte[]> images = new ArrayList<>();
    private String imageType;
}
