package propofol.tilservice.domain.board.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicUpdate
public class Recommend {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "recommend_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id", updatable = false)
    private Board board;

    private String memberId;

    public void addBoard(Board board){
        this.board = board;
    }

    @Builder(builderMethodName = "createRecommend")
    public Recommend(String memberId) {
        this.memberId = memberId;
    }
}
