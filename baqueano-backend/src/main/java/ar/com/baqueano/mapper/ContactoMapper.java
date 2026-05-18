package ar.com.baqueano.mapper;

import ar.com.baqueano.domain.Contacto;
import ar.com.baqueano.dto.contacto.ContactoCreateDTO;
import ar.com.baqueano.dto.contacto.ContactoListItemDTO;
import ar.com.baqueano.dto.contacto.ContactoResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface ContactoMapper {

    @Mapping(target = "ipOrigen", source = "ipOrigen")
    Contacto toEntity(ContactoCreateDTO dto, String ipOrigen);

    ContactoResponseDTO toResponseDTO(Contacto entity);

    ContactoListItemDTO toListItemDTO(Contacto entity);
}
