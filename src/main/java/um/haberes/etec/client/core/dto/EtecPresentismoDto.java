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
public class EtecPresentismoDto {

    private Long presentismoId;

    private Long asignacionCargoId;

    private Integer anho;

    private Integer mes;

    private String motivo;

    private Long legajoId;

    private Integer categoriaId;

    private Integer dependenciaId;

    private String categoriaNombre;

    private String dependenciaNombre;

    private String personaApellido;

    private String personaNombre;
}
