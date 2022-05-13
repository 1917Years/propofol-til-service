package propofol.tilservice.domain.file.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import propofol.tilservice.domain.board.entity.Board;
import propofol.tilservice.domain.entity.BaseEntity;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Image extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="file_id")
    private Long id;

    private String uploadFileName; // 업로드된 파일 이름
    private String storeFileName; // 저장 이름
    private String contentType; // 타입

    @Builder(builderMethodName = "createImage")
    public Image(String uploadFileName, String storeFileName, String contentType) {
        this.uploadFileName = uploadFileName;
        this.storeFileName = storeFileName;
        this.contentType = contentType;
    }
}
