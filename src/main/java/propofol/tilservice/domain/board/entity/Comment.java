package propofol.tilservice.domain.board.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;
import propofol.tilservice.domain.entity.BaseEntity;

import javax.persistence.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@DynamicUpdate
public class Comment extends BaseEntity {
    @Id @GeneratedValue
    @Column(name = "comment_id")
    private Long id;

    @Column(nullable = false)
    private String content;

//    @Column(nullable = false)
    private String nickname;

    private Long groupId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="board_id", updatable = false)
    private Board board;

    public void addGroupInfo(Long groupId) {
        this.groupId = groupId;
    }

    @Builder(builderMethodName = "createComment")
    public Comment(String content, String nickname, Board board) {
        this.content = content;
        this.nickname = nickname;
        this.board = board;
    }
}
