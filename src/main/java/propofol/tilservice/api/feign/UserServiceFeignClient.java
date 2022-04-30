package propofol.tilservice.api.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import propofol.tilservice.api.feign.dto.MemberInfoDto;
import propofol.tilservice.api.feign.dto.StreakResponseDto;

@FeignClient(name = "user-service")
public interface UserServiceFeignClient {

    @GetMapping("/api/v1/members")
    MemberInfoDto getMemberInfo(@RequestHeader(name = "Authorization") String token);

    @PostMapping("/api/v1/members/streak")
    void saveStreak(@RequestHeader(name = "Authorization") String token,
                    @RequestBody StreakResponseDto streakResponseDto);

}
