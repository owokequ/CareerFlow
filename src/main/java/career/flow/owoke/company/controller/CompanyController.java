package career.flow.owoke.company.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import career.flow.owoke.company.dto.request.CompanyCreateRequest;
import career.flow.owoke.company.dto.request.CompanyUpdateRequest;
import career.flow.owoke.company.dto.response.CompanyListResponse;
import career.flow.owoke.company.dto.response.CompanyResponse;
import career.flow.owoke.company.service.CompanyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/companies")
@RequiredArgsConstructor
public class CompanyController {

    private final CompanyService companyService;

    @PostMapping
    public ResponseEntity<CompanyResponse> createCompany(
            Authentication authentication,
            @Valid @RequestBody CompanyCreateRequest request) {
        CompanyResponse company = companyService.createCompany(authentication.getName(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(company);
    }

    @GetMapping
    public ResponseEntity<CompanyListResponse> getAllCompanies(Authentication authentication) {
        CompanyListResponse companies = companyService.getAllCompanies(authentication.getName());
        return ResponseEntity.ok(companies);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CompanyResponse> getCompanyById(
            Authentication authentication,
            @PathVariable String id) {
        CompanyResponse company = companyService.getCompanyById(authentication.getName(), id);
        return ResponseEntity.ok(company);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CompanyResponse> updateCompany(
            Authentication authentication,
            @PathVariable String id,
            @Valid @RequestBody CompanyUpdateRequest request) {
        CompanyResponse company = companyService.updateCompany(authentication.getName(), id, request);
        return ResponseEntity.ok(company);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCompany(Authentication authentication, @PathVariable String id) {
        companyService.deleteCompany(authentication.getName(), id);
    }
}
