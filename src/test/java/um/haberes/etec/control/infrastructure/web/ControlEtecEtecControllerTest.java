package um.haberes.etec.control.infrastructure.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import um.haberes.etec.client.core.dto.ControlEtecDto;
import um.haberes.etec.control.application.ControlEtecEtecService;

@ExtendWith(MockitoExtension.class)
class ControlEtecEtecControllerTest {

    @Mock
    private ControlEtecEtecService service;

    @InjectMocks
    private ControlEtecEtecController controller;

    @Test
    void devuelveElControlSinLosHeadersDelUpstream() {
        ControlEtecDto control = ControlEtecDto.builder()
                .anho(2026)
                .mes(11)
                .valorPuntoEtec(new BigDecimal("311.3633"))
                .montoFijoCargo(new BigDecimal("50000.00"))
                .build();
        when(service.findByPeriodoOpcional(2026, 11)).thenReturn(control);

        ResponseEntity<ControlEtecDto> respuesta = controller.findByPeriodoOpcional(2026, 11);

        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(respuesta.getBody()).isSameAs(control);
        assertThat(respuesta.getBody().getMontoFijoCargo()).isEqualByComparingTo("50000.00");
        // Regresión: reenviar el ResponseEntity de Feign copiaba Transfer-Encoding/Connection
        // y el gateway devolvía 500.
        assertThat(respuesta.getHeaders().isEmpty()).isTrue();
    }

    @Test
    void devuelve200SinCuerpoCuandoElPeriodoNoTieneControl() {
        when(service.findByPeriodoOpcional(2026, 10)).thenReturn(null);

        ResponseEntity<ControlEtecDto> respuesta = controller.findByPeriodoOpcional(2026, 10);

        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(respuesta.getBody()).isNull();
    }
}
