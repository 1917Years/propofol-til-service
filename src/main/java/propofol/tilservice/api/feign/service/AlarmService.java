package propofol.tilservice.api.feign.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import propofol.tilservice.api.feign.AlarmServiceFeignClient;
import propofol.tilservice.api.feign.AlarmType;
import propofol.tilservice.api.feign.dto.AlarmSaveDto;
import propofol.tilservice.api.feign.dto.AlarmSaveListDto;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AlarmService {

    private final AlarmServiceFeignClient alarmServiceFeignClient;

    public void saveAlarm(long toId, String message, String token, AlarmType type){
        alarmServiceFeignClient.saveAlarm(token, new AlarmSaveDto(toId, message, type, null));
    }

    public void saveListAlarm(List<Long> toIds, String message, String type, Long boardId, String token){
        AlarmSaveListDto alarmSaveListDto = new AlarmSaveListDto(toIds, message, type, boardId);
        System.out.println("alarmSaveListDto.getMessage() = " + alarmSaveListDto.getMessage());
        alarmServiceFeignClient.saveListAlarm(token, alarmSaveListDto);
    }
}
