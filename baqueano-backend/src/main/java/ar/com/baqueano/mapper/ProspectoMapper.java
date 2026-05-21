package ar.com.baqueano.mapper;

import ar.com.baqueano.domain.Prospecto;
import ar.com.baqueano.dto.prospecto.ProspectoCreateDTO;
import ar.com.baqueano.dto.prospecto.ProspectoListItemDTO;
import ar.com.baqueano.dto.prospecto.ProspectoResponseDTO;
import ar.com.baqueano.dto.prospecto.ProspectoUpdateDTO;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProspectoMapper {

    Prospecto toEntity(ProspectoCreateDTO dto);

    ProspectoResponseDTO toResponseDTO(Prospecto entity);

    ProspectoListItemDTO toListItemDTO(Prospecto entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(@MappingTarget Prospecto entity, ProspectoUpdateDTO dto);
}
