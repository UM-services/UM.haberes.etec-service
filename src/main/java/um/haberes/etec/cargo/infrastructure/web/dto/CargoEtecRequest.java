package um.haberes.etec.cargo.infrastructure.web.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class CargoEtecRequest {

    @NotBlank
    private String nombre;

    @NotNull
    @DecimalMin(value = "0.01")
    private BigDecimal puntos;
}
