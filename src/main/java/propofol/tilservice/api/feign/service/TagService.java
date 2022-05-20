package propofol.tilservice.api.feign.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import propofol.tilservice.api.feign.TagServiceFeignClient;
import propofol.tilservice.api.feign.dto.TagDto;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TagService {
    private final TagServiceFeignClient tagServiceFeignClient;

    public List<TagDto> getTagsByTagIds(String token, List<Long> ids){
        return tagServiceFeignClient.getTagsByTagId(token, ids).getTags();
    }
}
