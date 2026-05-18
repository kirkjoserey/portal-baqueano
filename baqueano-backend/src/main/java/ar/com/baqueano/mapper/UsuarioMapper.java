package ar.com.baqueano.mapper;

import ar.com.baqueano.domain.Perfil;
import ar.com.baqueano.domain.Usuario;
import ar.com.baqueano.dto.usuario.UsuarioCreateDTO;
import ar.com.baqueano.dto.usuario.UsuarioListItemDTO;
import ar.com.baqueano.dto.usuario.UsuarioResponseDTO;
import ar.com.baqueano.dto.usuario.UsuarioUpdateDTO;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface UsuarioMapper {

    /**
     * password y passwordHash quedan sin mapear: el service hashea el password
     * (BCrypt) y setea passwordHash explicitamente antes de persistir.
     *
     * nombre y activo estan en ambos sources (dto y perfil): explicito que
     * vienen del dto.
     */
    @Mapping(target = "nombre", source = "dto.nombre")
    @Mapping(target = "activo", source = "dto.activo")
    @Mapping(target = "perfil", source = "perfil")
    Usuario toEntity(UsuarioCreateDTO dto, Perfil perfil);

    @Mapping(target = "perfilId", source = "perfil.id")
    @Mapping(target = "perfilNombre", source = "perfil.nombre")
    UsuarioResponseDTO toResponseDTO(Usuario entity);

    @Mapping(target = "perfilNombre", source = "perfil.nombre")
    UsuarioListItemDTO toListItemDTO(Usuario entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "nombre", source = "dto.nombre")
    @Mapping(target = "activo", source = "dto.activo")
    @Mapping(target = "perfil", source = "perfil")
    void updateEntity(@MappingTarget Usuario entity, UsuarioUpdateDTO dto, Perfil perfil);
}
