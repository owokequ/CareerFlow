package career.flow.owoke.company.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import career.flow.owoke.company.entity.Company;

public interface CompanyRepository extends JpaRepository<Company, String> {

    List<Company> findAllByUserAuthId(String authId);

    Optional<Company> findByIdAndUserAuthId(String id, String authId);

    boolean existsByUserAuthIdAndNormalizedName(String authId, String normalizedName);

    boolean existsByUserAuthIdAndNormalizedNameAndIdNot(String authId, String normalizedName, String id);
}
