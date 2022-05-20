package propofol.tilservice.api.feign.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class TagsDto {
    private List<TagDto> tags = new ArrayList<>();
}
