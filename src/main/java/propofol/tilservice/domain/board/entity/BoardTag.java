package propofol.tilservice.domain.board.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BoardTag {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "board_tag_id")
    private Long id;

    private Long tagId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private Board board;

    public void changeBoard(Board board) {this.board = board;}

    @Builder(builderMethodName = "createTag")
    public BoardTag(Long tagId) {
        this.tagId = tagId;
    }
}
