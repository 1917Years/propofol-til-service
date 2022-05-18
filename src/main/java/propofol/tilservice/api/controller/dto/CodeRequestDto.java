package propofol.tilservice.api.controller.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class CodeRequestDto {
    @NotBlank
    private String type;
    @NotBlank
    private String content;
}
