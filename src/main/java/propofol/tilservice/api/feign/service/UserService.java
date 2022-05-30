package propofol.tilservice.api.feign.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import propofol.tilservice.api.controller.dto.ProfileImageResponseDto;
import propofol.tilservice.api.feign.UserServiceFeignClient;
import propofol.tilservice.api.feign.dto.MemberSaveBoardDto;
import propofol.tilservice.api.feign.dto.RecommendDto;
import propofol.tilservice.api.feign.dto.TagIdsDto;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserServiceFeignClient userServiceFeignClient;

    public String getUserNickName(String token, String memberId){
        return userServiceFeignClient.getMemberNickName(token, memberId);
    }

    public ProfileImageResponseDto getUserProfile(String token, String nickname) {
        return userServiceFeignClient.getCommentProfile(token, nickname);
    }

    public void saveMemberTag(String token, List<Long> tagIds){
        TagIdsDto tagIdsDto = new TagIdsDto();
        tagIdsDto.setTagIds(tagIds);
        userServiceFeignClient.saveMemberTag(token, tagIdsDto);
    }

    public void plusMemberTotalRecommend(String token, Long memberId){
        RecommendDto recommendDto = new RecommendDto();
        recommendDto.setId(memberId);
        userServiceFeignClient.plusMemberTotalRecommend(token, recommendDto);
    }

    public MemberSaveBoardDto getMyFollowerIdsAndNickname(String token, long memberId){
        return userServiceFeignClient.getMyFollowerIds(token, memberId);
    }
}