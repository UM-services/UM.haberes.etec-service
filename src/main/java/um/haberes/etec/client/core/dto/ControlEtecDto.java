package um.haberes.etec.client.core.dto;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Parámetros propios del circuito ETEC por puntos. El valor del punto y el monto fijo del
 * cargo viven en {@code control_etec} de haberes-core.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ControlEtecDto {

    private Integer anho;

    private Integer mes;

    private BigDecimal valorPuntoEtec;

    /** Importe fijo que se suma a {@code puntos × valorPuntoEtec} al liquidar el cargo. */
    private BigDecimal montoFijoCargo;

    /** Horas de referencia de los cargos por puntos del período. */
    private BigDecimal horasReferenciaCargo;

    /** Básico del maestro de grado del período. */
    private BigDecimal basicoMaestroGrado;
}
