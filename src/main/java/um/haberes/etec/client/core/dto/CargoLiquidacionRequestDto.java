package um.haberes.etec.client.core.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

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
public class CargoLiquidacionRequestDto {

    private Long legajoId;

    private Integer anho;

    private Integer mes;

    private Integer dependenciaId;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXX", timezone = "UTC")
    private OffsetDateTime fechaDesde;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXX", timezone = "UTC")
    private OffsetDateTime fechaHasta;

    private Integer categoriaId;

    private String categoriaNombre;

    private BigDecimal categoriaBasico;

    private BigDecimal estadoDocente;

    private BigDecimal horasJornada;

    private Integer jornada;

    private Integer presentismo;

    private String situacion;
}
