package propofol.tilservice.domain.file.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import propofol.tilservice.domain.board.entity.Board;
import propofol.tilservice.domain.exception.NotSaveFileException;
import propofol.tilservice.domain.file.entity.Image;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileService {

    public void saveBoardFile(String uploadDir, List<MultipartFile> files, Board board) throws IOException {

        String path = creatFolder(uploadDir, board);

        for (MultipartFile file : files) {
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
                    .createImage().storeFileName(storeFilename).uploadFileName(originalFilename).build();

            board.addImage(image);
        }

    }

    private String creatFolder(String uploadDir, Board board) {
        Path relativePath = Paths.get("");
        String path = relativePath.toAbsolutePath().toString() + "/" + uploadDir;
        File parentFolder = new File(path);

        if (!parentFolder.exists()){
            parentFolder.mkdir();
        }

        path = path + "/" + board.getId();
        File childFolder = new File(path);
        if(!childFolder.exists()){
            childFolder.mkdir();
        }
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



}
