package propofol.tilservice.domain.file.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import propofol.tilservice.domain.file.entity.Image;

import java.util.List;

public interface ImageRepository extends JpaRepository<Image, Long> {

//    @Query("select i from Image i where i.board.id=:boardId")
//    List<Image> findImages(@Param(value = "boardId") Long boardId);

//    @Modifying
//    @Query("delete from Image i where i.board.id=:boardId")
//    int deleteBulkImages(@Param(value = "boardId") Long boardId);w

    Image findImageByStoreFileName(String storeFileName);
}
