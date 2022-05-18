package propofol.tilservice.api.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import propofol.tilservice.api.common.properties.FileProperties;
import propofol.tilservice.domain.board.entity.Board;
import propofol.tilservice.domain.exception.NotFoundFileException;
import propofol.tilservice.domain.exception.NotSaveFileException;
import propofol.tilservice.domain.file.entity.Image;
import propofol.tilservice.domain.file.repository.ImageRepository;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
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
    public byte[] getImageBytes(String fileName){
        String path = findBoardPath(getUploadDir());
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

    public List<Image> getAllImage(List<String> fileOriginNames){
        return imageRepository.findImagesInNames(fileOriginNames);
    }

    public Image saveImage(MultipartFile file) throws IOException {
        String path = creatFolder();

        String originalFilename = file.getOriginalFilename();

        String extType = getExt(originalFilename);

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

        return imageRepository.save(image);
    }

    private String creatFolder() {
        String path = findBoardPath(getUploadDir());
        File parentFolder = new File(path);

        if (!parentFolder.exists()){
            parentFolder.mkdir();
        }

        return path;
    }

    public String getUploadDir() {
        return fileProperties.getBoardDir();
    }

    public String findBoardPath(String dir) {
        Path relativePath = Paths.get("");
        String path = relativePath.toAbsolutePath().toString() + "/" + dir;
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

    public Image findByImage(Long imageId) {
        Image image = imageRepository.findById(imageId).orElseThrow(() -> {
            throw new NotFoundFileException("파일을 찾을 수 없습니다.");
        });
        return image;
    }

    @Transactional
    public List<String> getStoreImageNames(List<MultipartFile> files) {
        String path = "http://localhost:8000/til-service/api/v1/images";
        List<String> fileNames = new ArrayList<>();
        files.forEach(file -> {
            try {
                Image saveImage = saveImage(file);
                fileNames.add(path + "/" + saveImage.getStoreFileName());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        return fileNames;
    }

    @Transactional
    public void changeImageBoardId(List<String> fileNames, Board saveBoard) {
        List<Image> images = getAllImage(fileNames);
        images.forEach(image -> {
            image.changeBoard(saveBoard);
        });
    }

    public Image getTopImage(Long boardId){
        return imageRepository.findTopByBoardId(boardId).orElse(null);
    }

    public byte[] getTopImageBytes(Image image){
        if(image == null) { return null; }
        return getImageBytes(image.getStoreFileName());
    }

    public String getImageType(Image image){
        if(image == null) { return null; }
        return image.getContentType();
    }

    public List<Image> getImagesByBoardId(Long boardId) {
        return imageRepository.findAllByBoardId(boardId);
    }

    @Transactional
    public void deleteImages(Long boardId){
        imageRepository.deleteImages(boardId);
    }
}
