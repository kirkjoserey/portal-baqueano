package ar.com.baqueano.mapper;

import ar.com.baqueano.domain.Perfil;
import ar.com.baqueano.dto.perfil.PerfilCreateDTO;
import ar.com.baqueano.dto.perfil.PerfilResponseDTO;
import ar.com.baqueano.dto.perfil.PerfilUpdateDTO;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PerfilMapper {

    Perfil toEntity(PerfilCreateDTO dto);

    PerfilResponseDTO toResponseDTO(Perfil entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(@MappingTarget Perfil entity, PerfilUpdateDTO dto);
}
