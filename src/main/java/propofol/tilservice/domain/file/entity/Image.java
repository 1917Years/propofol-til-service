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

    private String UploadFileName; // 업로드된 파일 이름
    private String StoreFileName; // 저장 이름

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id", updatable = false)
    private Board board;
    
    public void addBoard(Board board){
        this.board = board;
    }

    @Builder(builderMethodName = "createImage")
    public Image(String uploadFileName, String storeFileName) {
        UploadFileName = uploadFileName;
        StoreFileName = storeFileName;
    }
}
