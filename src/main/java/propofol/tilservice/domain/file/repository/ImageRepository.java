package propofol.tilservice.domain.file.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import propofol.tilservice.domain.file.entity.Image;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ImageRepository extends JpaRepository<Image, Long> {

//    @Query("select i from Image i where i.board.id=:boardId")
//    List<Image> findImages(@Param(value = "boardId") Long boardId);

    Image findImageByStoreFileName(String storeFileName);

    @Query("select i from Image i where i.storeFileName in :names")
    List<Image> findImagesInNames(@Param(value = "names") Collection<String> names);

    Optional<Image> findTopByBoardId(Long boardId);

    List<Image> findAllByBoardId(Long boardId);

    @Modifying(clearAutomatically = true)
    @Query("delete from Image i where i.board.id = :boardId")
    void deleteImages(@Param("boardId") Long boardId);
}
