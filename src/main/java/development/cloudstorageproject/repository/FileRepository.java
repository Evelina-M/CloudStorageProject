package development.cloudstorageproject.repository;

import development.cloudstorageproject.entity.FileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FileRepository extends JpaRepository<FileEntity, Long> {
    FileEntity findByFileNameAndOwner(String filename, String owner);

    List<FileEntity> findAllByOwner(String owner);
}