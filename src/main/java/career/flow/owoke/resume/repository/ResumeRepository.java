package career.flow.owoke.resume.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import career.flow.owoke.resume.entity.Resume;

public interface ResumeRepository extends JpaRepository<Resume, String> {

    List<Resume> findAllByUserAuthId(String authId);

    Optional<Resume> findByIdAndUserAuthId(String id, String authId);
}
