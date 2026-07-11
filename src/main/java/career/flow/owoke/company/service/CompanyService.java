package career.flow.owoke.company.service;

import java.util.Locale;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import career.flow.owoke.common.exception.companyExceptions.CompanyAlreadyExistsException;
import career.flow.owoke.common.exception.companyExceptions.CompanyNotFoundException;
import career.flow.owoke.common.exception.userExceptions.UserNotFoundException;
import career.flow.owoke.company.dto.request.CompanyCreateRequest;
import career.flow.owoke.company.dto.request.CompanyUpdateRequest;
import career.flow.owoke.company.dto.response.CompanyListResponse;
import career.flow.owoke.company.dto.response.CompanyResponse;
import career.flow.owoke.company.entity.Company;
import career.flow.owoke.company.mapper.CompanyMapper;
import career.flow.owoke.company.repository.CompanyRepository;
import career.flow.owoke.user.entity.User;
import career.flow.owoke.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CompanyService {

    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;
    private final CompanyMapper companyMapper;

    @Transactional
    public CompanyResponse createCompany(String authId, CompanyCreateRequest request) {
        String normalizedName = normalizeName(request.name());
        validateUniqueName(authId, normalizedName, null, request.name());

        User user = userRepository.findByAuthId(authId)
                .orElseThrow(() -> new UserNotFoundException(authId));

        Company company = companyMapper.toEntity(request);
        company.setUser(user);
        company.setName(displayName(request.name()));
        company.setNormalizedName(normalizedName);

        Company savedCompany = companyRepository.save(company);
        return companyMapper.toResponse(savedCompany);
    }

    @Transactional(readOnly = true)
    public CompanyListResponse getAllCompanies(String authId) {
        return new CompanyListResponse(
                companyRepository.findAllByUserAuthId(authId)
                        .stream()
                        .map(companyMapper::toResponse)
                        .toList());
    }

    @Transactional(readOnly = true)
    public CompanyResponse getCompanyById(String authId, String companyId) {
        return companyMapper.toResponse(findOwnedCompany(authId, companyId));
    }

    @Transactional
    public CompanyResponse updateCompany(String authId, String companyId, CompanyUpdateRequest request) {
        Company company = findOwnedCompany(authId, companyId);
        String normalizedName = normalizeName(request.name());
        validateUniqueName(authId, normalizedName, companyId, request.name());

        company.setName(displayName(request.name()));
        company.setNormalizedName(normalizedName);
        company.setWebsite(request.website());
        company.setIndustry(request.industry());
        company.setLocation(request.location());
        company.setDescription(request.description());

        return companyMapper.toResponse(company);
    }

    @Transactional
    public void deleteCompany(String authId, String companyId) {
        companyRepository.delete(findOwnedCompany(authId, companyId));
    }

    private Company findOwnedCompany(String authId, String companyId) {
        return companyRepository.findByIdAndUserAuthId(companyId, authId)
                .orElseThrow(() -> new CompanyNotFoundException(companyId));
    }

    private void validateUniqueName(String authId, String normalizedName, String excludedCompanyId, String name) {
        boolean companyExists = excludedCompanyId == null
                ? companyRepository.existsByUserAuthIdAndNormalizedName(authId, normalizedName)
                : companyRepository.existsByUserAuthIdAndNormalizedNameAndIdNot(authId, normalizedName,
                        excludedCompanyId);

        if (companyExists) {
            throw new CompanyAlreadyExistsException(name);
        }
    }

    private String displayName(String name) {
        return name.trim();
    }

    private String normalizeName(String name) {
        return displayName(name).toLowerCase(Locale.ROOT);
    }
}
