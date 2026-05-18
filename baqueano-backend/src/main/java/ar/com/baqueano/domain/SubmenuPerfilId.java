package ar.com.baqueano.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class SubmenuPerfilId implements Serializable {

    @Column(name = "submenu_id")
    private Long submenuId;

    @Column(name = "perfil_id")
    private Long perfilId;
}
