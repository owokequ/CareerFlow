package career.flow.owoke.company.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import career.flow.owoke.company.dto.request.CompanyCreateRequest;
import career.flow.owoke.company.dto.response.CompanyResponse;
import career.flow.owoke.company.entity.Company;

@Mapper(componentModel = "spring")
public interface CompanyMapper {

    @Mapping(target = "userId", source = "user.id")
    CompanyResponse toResponse(Company company);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "normalizedName", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Company toEntity(CompanyCreateRequest request);
}
