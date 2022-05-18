package propofol.tilservice.api.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import propofol.tilservice.api.controller.dto.ProfileImageResponseDto;
import propofol.tilservice.api.feign.dto.StreakResponseDto;

@FeignClient(name = "user-service")
public interface UserServiceFeignClient {

    @PostMapping("/api/v1/members/streak")
    void saveStreak(@RequestHeader(name = "Authorization") String token,
                    @RequestBody StreakResponseDto streakResponseDto);

    @GetMapping("/api/v1/members/{memberId}")
    String getMemberNickName(@RequestHeader(name = "Authorization") String token,
                             @PathVariable("memberId") String memberId);

    @PostMapping("/api/v1/members/commentProfile")
    ProfileImageResponseDto getCommentProfile(
            @RequestHeader(name="Authorization") String token,
            @RequestBody String nickname);

}