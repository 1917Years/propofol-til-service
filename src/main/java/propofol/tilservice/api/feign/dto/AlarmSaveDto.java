package propofol.tilservice.api.feign.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import propofol.tilservice.api.feign.AlarmType;

@Data
@AllArgsConstructor
public class AlarmSaveDto {
    private long toId;
    private String message;
    private AlarmType type;
    private Long boardId;
}
