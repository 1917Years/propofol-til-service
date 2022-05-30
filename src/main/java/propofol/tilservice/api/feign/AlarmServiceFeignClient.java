package propofol.tilservice.api.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import propofol.tilservice.api.feign.dto.AlarmSaveDto;
import propofol.tilservice.api.feign.dto.AlarmSaveListDto;

@FeignClient("alarm-service")
public interface AlarmServiceFeignClient {

    @PostMapping("api/v1/alarms")
    void saveAlarm(@RequestHeader(value = "Authorization", required = false) String token,
                   @RequestBody AlarmSaveDto alarmSaveDto);

    @PostMapping("api/v1/alarms/list")
    void saveListAlarm(@RequestHeader(value = "Authorization", required = false) String token,
                       @RequestBody AlarmSaveListDto alarmSaveListDto);
}
