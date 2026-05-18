package ar.com.baqueano.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "parametros")
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(of = "id", callSuper = false)
@ToString(of = {"id", "clave"})
public class Parametro extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 100)
    @Column(nullable = false, length = 100, unique = true)
    private String clave;

    @NotBlank
    @Size(max = 500)
    @Column(nullable = false, length = 500)
    private String valor;

    @Size(max = 255)
    @Column(length = 255)
    private String descripcion;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_dato", nullable = false)
    private TipoDato tipoDato = TipoDato.STRING;

    @Column(nullable = false)
    private Boolean editable = Boolean.TRUE;

    public enum TipoDato {
        STRING, NUMBER, BOOLEAN, JSON, DATE
    }
}
