package propofol.tilservice.api.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import propofol.tilservice.api.feign.UserServiceFeignClient;
import propofol.tilservice.api.feign.dto.StreakResponseDto;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class StreakService {

    private final UserServiceFeignClient userServiceFeignClient;

    public void saveStreak(String token){
        StreakResponseDto streakResponseDto = new StreakResponseDto();
        streakResponseDto.setDate(LocalDate.now());
        streakResponseDto.setWorking(true);

        userServiceFeignClient.saveStreak(token, streakResponseDto);
    }
}
