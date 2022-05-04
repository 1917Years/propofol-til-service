package propofol.tilservice.api.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import propofol.tilservice.api.controller.dto.ImagesResponseDto;
import propofol.tilservice.api.controller.dto.ResponseDto;
import propofol.tilservice.domain.file.entity.Image;
import propofol.tilservice.domain.file.service.ImageService;

import java.net.MalformedURLException;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/images")
public class ImageController {

    private final ImageService imageService;

    @GetMapping("/{boardId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseDto getImages(@PathVariable("boardId") Long boardId) {
        return new ResponseDto<>(HttpStatus.OK.value(), "success",
                "이미지 조회 성공!", imageService.getImages(boardId));
    }

    /**
     * 포스트맨으로 확인 불가
     * 프론트 구현 시 확인
     */
//    @GetMapping(value = "/{boardId}/{imageId}")
//    @ResponseStatus(HttpStatus.OK)
//    public ResponseDto getImage(@PathVariable("boardId") Long boardId,
//                                     @PathVariable("imageId") Long imageId) throws Exception {
//        ResponseImageDto imageDto = imageService.getImage(boardId, imageId);
//    return new ResponseDto<>(HttpStatus.OK.value(), "success",
//            "이미지 조회 성공!", imageDto.getImage());
//    }

    /**
     * 프론트 어딨냐!!!!!!!
     */
    @GetMapping("/{boardId}/{imageId}")
    @ResponseStatus(HttpStatus.OK)
        public ResponseDto getImage(@PathVariable("boardId") Long boardId,
                @PathVariable("imageId") Long imageId) throws MalformedURLException {

        Image image = imageService.findByImage(imageId);
        String storeFileName1 = image.getStoreFileName();
        String boardPath = imageService.findBoardPath();

        UrlResource resource = new UrlResource("file:" + boardPath + "/" + boardId + "/" + storeFileName1);
        return new ResponseDto<>(HttpStatus.OK.value(), "success",
                "이미지 조회 성공!", resource);
    }
}
