package ar.com.baqueano.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "prospectos")
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(of = "id", callSuper = false)
@ToString(of = {"id", "nombre", "apellido", "email"})
public class Prospecto extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String nombre;

    @NotBlank
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String apellido;

    @Size(max = 150)
    @Column(length = 150)
    private String empresa;

    @NotBlank
    @Email
    @Size(max = 150)
    @Column(nullable = false, length = 150)
    private String email;

    @Size(max = 30)
    @Column(length = 30)
    private String telefono;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Estado estado = Estado.NUEVO;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Origen origen = Origen.WEB;

    @Column(columnDefinition = "TEXT")
    private String notas;

    @Column(nullable = false)
    private Boolean activo = Boolean.TRUE;

    public enum Estado {
        NUEVO, CONTACTADO, CALIFICADO, PERDIDO, CONVERTIDO
    }

    public enum Origen {
        REFERIDO, WEB, RED_SOCIAL, LLAMADA, EMAIL, OTRO
    }
}
