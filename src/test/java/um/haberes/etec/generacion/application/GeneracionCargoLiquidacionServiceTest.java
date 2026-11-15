package um.haberes.etec.generacion.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import um.haberes.etec.client.core.CoreCargoLiquidacionClient;
import um.haberes.etec.client.core.CoreCategoriaClient;
import um.haberes.etec.client.core.CoreControlEtecClient;
import um.haberes.etec.client.core.dto.CargoLiquidacionDto;
import um.haberes.etec.client.core.dto.CategoriaDto;
import um.haberes.etec.client.core.dto.ControlEtecDto;
import um.haberes.etec.shared.exception.EtecValidationException;

@ExtendWith(MockitoExtension.class)
class GeneracionCargoLiquidacionServiceTest {

    @Mock
    private CoreControlEtecClient controlEtecClient;

    @Mock
    private CoreCategoriaClient categoriaClient;

    @Mock
    private CoreCargoLiquidacionClient cargoLiquidacionClient;

    @InjectMocks
    private GeneracionCargoLiquidacionService service;

    private CategoriaDto directoraEtec() {
        return CategoriaDto.builder()
                .categoriaId(9001)
                .nombre("Directora ETEC")
                .docente(1)
                .noDocente(0)
                .basico(BigDecimal.ZERO)
                .puntos(new BigDecimal("1000.00"))
                .build();
    }

    private ControlEtecDto control(String valor) {
        return ControlEtecDto.builder().anho(2026).mes(11).valorPuntoEtec(new BigDecimal(valor)).build();
    }

    private CargoLiquidacionDto cargoCalculado() {
        return CargoLiquidacionDto.builder()
                .cargoLiquidacionId(10L)
                .legajoId(1234L)
                .anho(2026)
                .mes(11)
                .dependenciaId(1)
                .fechaDesde(OffsetDateTime.of(2026, 11, 1, 0, 0, 0, 0, ZoneOffset.UTC))
                .fechaHasta(OffsetDateTime.of(2026, 11, 30, 0, 0, 0, 0, ZoneOffset.UTC))
                .categoriaId(9001)
                .categoriaNombre("Directora ETEC")
                .categoriaBasico(new BigDecimal("3000000.00"))
                .estadoDocente(BigDecimal.ZERO)
                .horasJornada(BigDecimal.ZERO)
                .jornada(1)
                .presentismo(0)
                .situacion("A")
                .build();
    }

    @Test
    void delegaLaGeneracionDelPeriodoEnCore() {
        when(categoriaClient.findConPuntos()).thenReturn(List.of(directoraEtec()));
        when(controlEtecClient.findByPeriodoOpcional(2026, 11))
                .thenReturn(ResponseEntity.ok(control("3000.00")));
        when(cargoLiquidacionClient.generarEtecByPeriodo(2026, 11)).thenReturn(List.of(cargoCalculado()));

        List<CargoLiquidacionDto> cargos = service.generar(2026, 11);

        assertThat(cargos).hasSize(1);
        assertThat(cargos.getFirst().getCategoriaBasico()).isEqualByComparingTo("3000000.00");
        verify(cargoLiquidacionClient).generarEtecByPeriodo(2026, 11);
        verify(cargoLiquidacionClient, never()).replaceByPeriodo(org.mockito.ArgumentMatchers.any(), anyInt(), anyInt());
    }

    @Test
    void delegaLaGeneracionDelLegajoEnCore() {
        when(categoriaClient.findConPuntos()).thenReturn(List.of(directoraEtec()));
        when(controlEtecClient.findByPeriodoOpcional(2026, 11))
                .thenReturn(ResponseEntity.ok(control("3000.00")));
        when(cargoLiquidacionClient.generarEtecByLegajo(1234L, 2026, 11)).thenReturn(List.of(cargoCalculado()));

        List<CargoLiquidacionDto> cargos = service.generarByLegajo(1234L, 2026, 11);

        assertThat(cargos).hasSize(1);
        verify(cargoLiquidacionClient).generarEtecByLegajo(1234L, 2026, 11);
    }

    @Test
    void previewMapeaLaRespuestaDeCoreSinEscribir() {
        when(categoriaClient.findConPuntos()).thenReturn(List.of(directoraEtec()));
        when(controlEtecClient.findByPeriodoOpcional(2026, 11))
                .thenReturn(ResponseEntity.ok(control("3000.00")));
        when(cargoLiquidacionClient.previewEtecByPeriodo(2026, 11)).thenReturn(List.of(cargoCalculado()));

        var preview = service.preview(2026, 11);

        assertThat(preview).hasSize(1);
        assertThat(preview.getFirst().getCategoriaBasico()).isEqualByComparingTo("3000000.00");
        assertThat(preview.getFirst().getSituacion()).isEqualTo("A");
        verify(cargoLiquidacionClient, never()).generarEtecByPeriodo(anyInt(), anyInt());
        verify(cargoLiquidacionClient, never()).generarEtecByLegajo(org.mockito.ArgumentMatchers.anyLong(),
                anyInt(), anyInt());
    }

    @Test
    void fallaSinValorDePunto() {
        when(categoriaClient.findConPuntos()).thenReturn(List.of(directoraEtec()));
        when(controlEtecClient.findByPeriodoOpcional(2026, 11))
                .thenReturn(ResponseEntity.ok(control("0.00")));

        assertThrows(EtecValidationException.class, () -> service.preview(2026, 11));
        verifyNoInteractions(cargoLiquidacionClient);
    }

    @Test
    void fallaSinCargosConPuntosConfigurados() {
        when(categoriaClient.findConPuntos()).thenReturn(List.of());

        assertThrows(EtecValidationException.class, () -> service.generar(2026, 11));
        verifyNoInteractions(cargoLiquidacionClient);
        verifyNoInteractions(controlEtecClient);
    }
}
