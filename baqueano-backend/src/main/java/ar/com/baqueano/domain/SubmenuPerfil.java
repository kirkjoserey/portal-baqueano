package ar.com.baqueano.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "submenu_perfil")
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
@ToString(of = {"id", "puedeVer", "puedeCrear", "puedeEditar", "puedeEliminar"})
public class SubmenuPerfil {

    @EmbeddedId
    private SubmenuPerfilId id = new SubmenuPerfilId();

    @NotNull
    @MapsId("submenuId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "submenu_id", nullable = false)
    private Submenu submenu;

    @NotNull
    @MapsId("perfilId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "perfil_id", nullable = false)
    private Perfil perfil;

    @Column(name = "puede_ver", nullable = false)
    private Boolean puedeVer = Boolean.TRUE;

    @Column(name = "puede_crear", nullable = false)
    private Boolean puedeCrear = Boolean.FALSE;

    @Column(name = "puede_editar", nullable = false)
    private Boolean puedeEditar = Boolean.FALSE;

    @Column(name = "puede_eliminar", nullable = false)
    private Boolean puedeEliminar = Boolean.FALSE;
}
