package um.haberes.etec.client.core.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoriaRequestDto {

    private Integer categoriaId;

    private String nombre;

    private BigDecimal basico;

    private Integer docente;

    private Integer noDocente;

    private Integer liquidaPorHora;

    private BigDecimal estadoDocente;

    private BigDecimal puntos;
}
