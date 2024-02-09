package gdsc.skhu.drugescape.domain.repository;

import gdsc.skhu.drugescape.domain.model.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ImageRepository extends JpaRepository<Image, Long> {
}