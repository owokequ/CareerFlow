package career.flow.owoke.resume.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import career.flow.owoke.common.exception.resumeExceptions.ResumeNotFoundException;
import career.flow.owoke.common.exception.userExceptions.UserNotFoundException;
import career.flow.owoke.resume.dto.request.ResumeCreateRequest;
import career.flow.owoke.resume.dto.request.ResumeUpdateRequest;
import career.flow.owoke.resume.dto.response.ResumeListResponse;
import career.flow.owoke.resume.dto.response.ResumeResponse;
import career.flow.owoke.resume.entity.Resume;
import career.flow.owoke.resume.mapper.ResumeMapper;
import career.flow.owoke.resume.repository.ResumeRepository;
import career.flow.owoke.user.entity.User;
import career.flow.owoke.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ResumeService {

    private final ResumeRepository resumeRepository;
    private final UserRepository userRepository;
    private final ResumeMapper resumeMapper;

    @Transactional
    public ResumeResponse createResume(String authId, ResumeCreateRequest request) {
        User user = userRepository.findByAuthId(authId).orElseThrow(() -> new UserNotFoundException(authId));
        Resume resume = resumeMapper.toEntity(request);
        resume.setUser(user);
        resumeRepository.save(resume);
        return resumeMapper.toResponse(resume);
    }

    @Transactional(readOnly = true)
    public ResumeListResponse getAllResumes(String authId) {
        return new ResumeListResponse(
                resumeRepository.findAllByUserAuthId(authId).stream().map(resumeMapper::toResponse).toList());
    }

    @Transactional(readOnly = true)
    public ResumeResponse getResumeById(String authId, String resumeId) {
        Resume resume = resumeRepository.findByIdAndUserAuthId(resumeId, authId)
                .orElseThrow(() -> new ResumeNotFoundException(resumeId));
        return resumeMapper.toResponse(resume);
    }

    @Transactional
    public ResumeResponse updateResume(String authId, String resumeId, ResumeUpdateRequest request) {
        Resume resume = resumeRepository.findByIdAndUserAuthId(resumeId, authId)
                .orElseThrow(() -> new ResumeNotFoundException(resumeId));
        resume.setTitle(request.title());
        resume.setTargetPosition(request.targetPosition());
        resume.setSkills(request.skills());
        resume.setSummary(request.summary());

        return resumeMapper.toResponse(resume);
    }

    @Transactional
    public void deleteResume(String authId, String resumeId) {
        Resume resume = resumeRepository.findByIdAndUserAuthId(resumeId, authId)
                .orElseThrow(() -> new ResumeNotFoundException(resumeId));
        resumeRepository.delete(resume);
    }
}
