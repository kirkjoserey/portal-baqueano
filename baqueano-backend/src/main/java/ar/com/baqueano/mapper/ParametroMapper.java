package ar.com.baqueano.mapper;

import ar.com.baqueano.domain.Parametro;
import ar.com.baqueano.dto.parametro.ParametroCreateDTO;
import ar.com.baqueano.dto.parametro.ParametroResponseDTO;
import ar.com.baqueano.dto.parametro.ParametroUpdateDTO;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ParametroMapper {

    Parametro toEntity(ParametroCreateDTO dto);

    ParametroResponseDTO toResponseDTO(Parametro entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(@MappingTarget Parametro entity, ParametroUpdateDTO dto);
}
