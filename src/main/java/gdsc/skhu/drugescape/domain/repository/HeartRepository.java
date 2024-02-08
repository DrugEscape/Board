package gdsc.skhu.drugescape.domain.repository;

import gdsc.skhu.drugescape.domain.model.Heart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HeartRepository extends JpaRepository<Heart, Long> {
}
