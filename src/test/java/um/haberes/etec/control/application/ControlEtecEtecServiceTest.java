package um.haberes.etec.control.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import um.haberes.etec.client.core.CoreControlEtecClient;
import um.haberes.etec.client.core.dto.ControlEtecDto;

@ExtendWith(MockitoExtension.class)
class ControlEtecEtecServiceTest {

    @Mock
    private CoreControlEtecClient client;

    @InjectMocks
    private ControlEtecEtecService service;

    @Test
    void devuelveElCuerpoDelControlDelPeriodo() {
        ControlEtecDto control = ControlEtecDto.builder()
                .anho(2026)
                .mes(11)
                .valorPuntoEtec(new BigDecimal("311.3633"))
                .build();
        when(client.findByPeriodoOpcional(2026, 11)).thenReturn(ResponseEntity.ok(control));

        ControlEtecDto resultado = service.findByPeriodoOpcional(2026, 11);

        assertThat(resultado).isSameAs(control);
    }

    @Test
    void devuelveNullCuandoElPeriodoNoTieneControl() {
        when(client.findByPeriodoOpcional(2026, 10)).thenReturn(ResponseEntity.<ControlEtecDto>ok(null));

        ControlEtecDto resultado = service.findByPeriodoOpcional(2026, 10);

        assertThat(resultado).isNull();
    }
}
