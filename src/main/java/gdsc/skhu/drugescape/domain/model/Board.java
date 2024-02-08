package gdsc.skhu.drugescape.domain.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Builder(toBuilder = true)
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Board extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;   // 제목

    @Column(nullable = false)
    private String body;   // 내용

    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;      // 작성자

    @OneToMany(mappedBy = "board", orphanRemoval = true)
    private List<Comment> comments; // 댓글
    private Integer commentCnt;     // 댓글 수

    @OneToMany(mappedBy = "board", orphanRemoval = true)
    private List<Heart> hearts;       // 좋아요
    private Integer heartCnt;        // 좋아요 수

    @OneToOne(fetch = FetchType.LAZY)
    private Image image;

    public void heartChange(Integer heartCnt) {
        this.heartCnt = heartCnt;
    }

    public void commentChange(Integer commentCnt) {
        this.commentCnt = commentCnt;
    }

    public void uploadImage(Image uploadImage) {
        this.image = uploadImage;
    }
}