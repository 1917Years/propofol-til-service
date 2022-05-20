package propofol.tilservice.api.feign.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class TagIdsDto {
    private List<Long> tagIds = new ArrayList<>();
}
