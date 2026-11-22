package um.haberes.etec.client.core.dto;

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
public class EtecPresentismoRequestDto {

    private Long asignacionCargoId;

    private Integer anho;

    private Integer mes;

    private String motivo;
}
