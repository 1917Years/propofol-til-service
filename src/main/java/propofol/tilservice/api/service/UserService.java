package propofol.tilservice.api.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import propofol.tilservice.api.feign.UserServiceFeignClient;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserServiceFeignClient userServiceFeignClient;

    public String getUserNickName(String token, String memberId){
        return userServiceFeignClient.getMemberNickName(token, memberId);
    }
}
