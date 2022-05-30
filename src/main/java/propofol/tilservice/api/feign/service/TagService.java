package propofol.tilservice.api.feign.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import propofol.tilservice.api.feign.TagServiceFeignClient;
import propofol.tilservice.api.feign.dto.TagDto;

import java.util.*;

@Service
@RequiredArgsConstructor
public class TagService {
    private final TagServiceFeignClient tagServiceFeignClient;

    public List<TagDto> getTagsByTagIds(String token, Set<Long> ids){
        if(ids.size() == 0) return new ArrayList<>();
        return new ArrayList<>(tagServiceFeignClient.getTagsByTagId(token, ids).getTags());
    }
}
