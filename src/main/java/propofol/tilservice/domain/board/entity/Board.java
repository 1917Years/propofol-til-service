package propofol.tilservice.domain.board.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;
import propofol.tilservice.domain.entity.BaseEntity;
import propofol.tilservice.domain.file.entity.Image;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@DynamicUpdate
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Board extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "board_id")
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;

    private Integer recommend; // 추천수

    @Column(nullable = false)
    private Boolean open;

    @OneToMany(mappedBy = "board")
    private List<Recommend> recommends = new ArrayList<>();

    @OneToMany(mappedBy = "board", cascade = CascadeType.PERSIST)
    private List<Image> images = new ArrayList<>();

    public void addImage(Image image){
        images.add(image);
        image.addBoard(this);
    }

    public void addRecommend(Recommend recommend){
        recommend.addBoard(this);
        recommends.add(recommend);
    }

    public void setDownRecommend(){
        this.recommend = this.recommend - 1;
    }

    public void setUpRecommend(){
        this.recommend = this.recommend + 1;
    }

    @Builder(builderMethodName = "createBoard")
    public Board(String title, String content, Integer recommend, Boolean open) {
        this.title = title;
        this.content = content;
        this.recommend = recommend;
        this.open = open;
    }

    public void updateBoard(String title, String content, Boolean open){
        if(title != null) this.title = title;
        if(content != null) this.content = content;
        this.open = open;
    }
}
