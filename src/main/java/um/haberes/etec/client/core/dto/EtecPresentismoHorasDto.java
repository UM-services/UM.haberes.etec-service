package um.haberes.etec.client.core.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

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
@JsonIgnoreProperties(ignoreUnknown = true)
public class EtecPresentismoHorasDto {

    private Long presentismoHorasId;

    private Long legajoId;

    private Integer anho;

    private Integer mes;

    private String motivo;

    private String personaApellido;

    private String personaNombre;

    private boolean tieneCargosHoras;
}
