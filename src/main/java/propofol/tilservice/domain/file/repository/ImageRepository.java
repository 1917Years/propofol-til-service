package propofol.tilservice.domain.file.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import propofol.tilservice.domain.file.entity.Image;

public interface ImageRepository extends JpaRepository<Image, Long> {

}
