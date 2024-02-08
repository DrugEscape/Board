package gdsc.skhu.drugescape.domain.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Builder(toBuilder = true)
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String originalFilename;    // 원본 파일명

    @Column(nullable = false)
    private String savedFilename;        // 서버에 저장된 파일명

    @OneToOne(mappedBy = "image", fetch = FetchType.LAZY)
    private Board board;
}