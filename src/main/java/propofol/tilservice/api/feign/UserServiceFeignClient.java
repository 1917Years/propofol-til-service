package propofol.tilservice.api.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import propofol.tilservice.api.feign.dto.MemberInfoDto;

@FeignClient(name = "user-service")
public interface UserServiceFeignClient {

    @GetMapping("/api/v1/members")
    MemberInfoDto getMemberInfo(@RequestHeader(name = "Authorization") String token);
}
