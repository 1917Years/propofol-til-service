package propofol.tilservice.api.feign.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import propofol.tilservice.api.feign.AlarmType;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
public class AlarmSaveListDto {
    private List<Long> toIds = new ArrayList<>();
    private String message;
    private String type;
    private Long boardIds;
}
