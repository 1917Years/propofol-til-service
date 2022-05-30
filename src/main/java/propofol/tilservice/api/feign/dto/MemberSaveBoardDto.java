package propofol.tilservice.api.feign.dto;

import lombok.Data;

import java.util.Set;

@Data
public class MemberSaveBoardDto {
    private String nickname;
    private Set<Long> memberIds;
}
