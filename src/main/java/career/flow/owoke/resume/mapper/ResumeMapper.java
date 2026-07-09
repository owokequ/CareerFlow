package career.flow.owoke.resume.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import career.flow.owoke.resume.dto.request.ResumeCreateRequest;
import career.flow.owoke.resume.dto.response.ResumeResponse;
import career.flow.owoke.resume.entity.Resume;

@Mapper(componentModel = "spring")
public interface ResumeMapper {

    @Mapping(target = "userId", source = "user.id")
    ResumeResponse toResponse(Resume resume);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Resume toEntity(ResumeCreateRequest request);

}
