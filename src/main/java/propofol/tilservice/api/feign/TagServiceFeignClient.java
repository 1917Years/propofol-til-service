package propofol.tilservice.api.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import propofol.tilservice.api.feign.dto.TagsDto;

import java.util.List;

@FeignClient("tag-service")
public interface TagServiceFeignClient {

    @GetMapping("/api/v1/tags/ids")
    TagsDto getTagsByTagId(@RequestHeader("Authorization") String token,
                           @RequestParam("ids") List<Long> ids);
}
