package career.flow.owoke.resume.controller;

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

import career.flow.owoke.resume.dto.request.ResumeCreateRequest;
import career.flow.owoke.resume.dto.request.ResumeUpdateRequest;
import career.flow.owoke.resume.dto.response.ResumeListResponse;
import career.flow.owoke.resume.dto.response.ResumeResponse;
import career.flow.owoke.resume.service.ResumeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/resumes")
public class ResumeController {

    private final ResumeService resumeService;

    @PostMapping
    public ResponseEntity<ResumeResponse> createResume(
            Authentication authentication,
            @Valid @RequestBody ResumeCreateRequest request) {
        ResumeResponse resume = resumeService.createResume(authentication.getName(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(resume);
    }

    @GetMapping
    public ResponseEntity<ResumeListResponse> getAllResumes(Authentication authentication) {
        ResumeListResponse resumes = resumeService.getAllResumes(authentication.getName());
        return ResponseEntity.ok(resumes);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResumeResponse> getResumeById(
            Authentication authentication,
            @PathVariable String id) {
        ResumeResponse resume = resumeService.getResumeById(authentication.getName(), id);
        return ResponseEntity.ok(resume);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResumeResponse> updateResume(
            Authentication authentication,
            @PathVariable String id,
            @Valid @RequestBody ResumeUpdateRequest request) {
        ResumeResponse resume = resumeService.updateResume(authentication.getName(), id, request);
        return ResponseEntity.ok(resume);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteResume(Authentication authentication, @PathVariable String id) {
        resumeService.deleteResume(authentication.getName(), id);
    }
}
