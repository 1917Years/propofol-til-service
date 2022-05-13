package propofol.tilservice.domain.file.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import propofol.tilservice.api.common.properties.FileProperties;
import propofol.tilservice.api.controller.dto.ImageResponseDto;
import propofol.tilservice.domain.exception.NotFoundFileException;
import propofol.tilservice.domain.exception.NotSaveFileException;
import propofol.tilservice.domain.file.entity.Image;
import propofol.tilservice.domain.file.repository.ImageRepository;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImageService {

    private final ImageRepository imageRepository;
    private final FileProperties fileProperties;

    /**
     * 이미지를 바이트로 변환해서 전달 -> 링크
     * 클라이언트에서 타입을 지정해서 보여줄 수 있으면 사용
     */
    public byte[] getImages(String fileName){
        String path = findBoardPath();
        byte[] bytes = null;

        try {
            String file = path + "/" + fileName;

            InputStream imageStream = new FileInputStream(file);
            bytes = IOUtils.toByteArray(imageStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return bytes;
    }

    public String saveImage(MultipartFile file) throws IOException {

        String path = creatFolder();

        // cat
        String originalFilename = file.getOriginalFilename();

        // png
        String extType = getExt(originalFilename);

        // asfwfef1212314.png
        String storeFilename = createStoreFilename(extType);

        try {
            file.transferTo(new File(getFullPath(path, storeFilename)));
        } catch (IOException e) {
            throw new NotSaveFileException("파일을 저장할 수 없습니다.");
        }
        Image image = Image
                .createImage().storeFileName(storeFilename)
                .contentType(file.getContentType())
                .uploadFileName(originalFilename)
                .build();

        imageRepository.save(image);

        return storeFilename;
    }

    private String creatFolder() {
        String path = findBoardPath();
        File parentFolder = new File(path);

        if (!parentFolder.exists()){
            parentFolder.mkdir();
        }

        return path;
    }

    public String findBoardPath() {
        String uploadDir = fileProperties.getBoardDir();
        Path relativePath = Paths.get("");
        String path = relativePath.toAbsolutePath().toString() + "/" + uploadDir;
        return path;
    }

    public String getFullPath(String path, String filename){
        return path + "/" + filename;
    }

    private String getExt(String originalFilename){
        int pos = originalFilename.lastIndexOf(".");
        return originalFilename.substring(pos + 1);
    }

    private String createStoreFilename(String extType) {
        String uuid = UUID.randomUUID().toString();
        return uuid + "." + extType;
    }


    public ImageResponseDto getImage(Long boardId, Long imageId) throws Exception {
        ImageResponseDto responseImageDto = new ImageResponseDto();
        String boardDir = fileProperties.getBoardDir();
        String path = findBoardPath();
        FileInputStream inputStream = null;
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        Image image = findByImage(imageId);

        String storeFileName = image.getStoreFileName();
        try {
            String file = path + "/" + boardId + "/" + storeFileName;
            inputStream = new FileInputStream(file);
        }catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        int readCount = 0;
        byte[] buffer = new byte[1024];
        byte[] fileArray = null;

        try {
            while((readCount = inputStream.read(buffer)) != -1){
                outputStream.write(buffer, 0, readCount);
            }
            fileArray = outputStream.toByteArray();
            responseImageDto.setImage(fileArray);
            responseImageDto.setImageType(image.getContentType());
            inputStream.close();
            outputStream.close();
        } catch (IOException e) {
            throw new Exception("파일을 변환하는데 문제가 발생했습니다.");
        }

        return responseImageDto;
    }

    public Image findByImage(Long imageId) {
        Image image = imageRepository.findById(imageId).orElseThrow(() -> {
            throw new NotFoundFileException("파일을 찾을 수 없습니다.");
        });
        return image;
    }

//    @Transactional
//    public void deleteImages(Long boardId){
//        imageRepository.deleteBulkImages(boardId);
//    }
}
