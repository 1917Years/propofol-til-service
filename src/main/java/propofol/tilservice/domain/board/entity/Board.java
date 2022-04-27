package propofol.tilservice.domain.board.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import propofol.tilservice.domain.entity.BaseEntity;

import javax.persistence.*;

@Entity
@Getter
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
