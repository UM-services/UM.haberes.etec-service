package um.haberes.etec.generacion.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import um.haberes.etec.client.core.CoreCargoLiquidacionClient;
import um.haberes.etec.client.core.CoreCategoriaClient;
import um.haberes.etec.client.core.CoreControlClient;
import um.haberes.etec.client.core.CoreEtecAsignacionCargoClient;
import um.haberes.etec.client.core.dto.CategoriaDto;
import um.haberes.etec.client.core.dto.ControlDto;
import um.haberes.etec.client.core.dto.EtecAsignacionCargoDto;
import um.haberes.etec.client.core.dto.ReemplazoCargosRequestDto;
import um.haberes.etec.shared.exception.EtecValidationException;

@ExtendWith(MockitoExtension.class)
class GeneracionCargoLiquidacionServiceTest {

    @Mock
    private CoreControlClient controlClient;

    @Mock
    private CoreCategoriaClient categoriaClient;

    @Mock
    private CoreEtecAsignacionCargoClient asignacionClient;

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

    private EtecAsignacionCargoDto asignacion() {
        return EtecAsignacionCargoDto.builder()
                .asignacionCargoId(1L)
                .legajoId(1234L)
                .categoriaId(9001)
                .dependenciaId(1)
                .fechaAlta(OffsetDateTime.of(2026, 11, 1, 0, 0, 0, 0, ZoneOffset.UTC))
                .build();
    }

    private ControlDto control(String valor) {
        return ControlDto.builder().anho(2026).mes(11).valorPuntoEtec(new BigDecimal(valor)).build();
    }

    @Test
    void calculaPuntosPorValorDelPunto() {
        when(controlClient.findByPeriodo(2026, 11)).thenReturn(control("3000.00"));
        when(categoriaClient.findConPuntos()).thenReturn(List.of(directoraEtec()));
        when(asignacionClient.findVigentesByPeriodo(2026, 11)).thenReturn(List.of(asignacion()));
        when(asignacionClient.findCategoriasAsignadas()).thenReturn(List.of(9001));
        when(cargoLiquidacionClient.replaceByPeriodo(any(), eq(2026), eq(11))).thenReturn(List.of());

        service.generar(2026, 11);

        ArgumentCaptor<ReemplazoCargosRequestDto> captor =
                ArgumentCaptor.forClass(ReemplazoCargosRequestDto.class);
        verify(cargoLiquidacionClient).replaceByPeriodo(captor.capture(), eq(2026), eq(11));

        ReemplazoCargosRequestDto request = captor.getValue();
        assertEquals(List.of(9001), request.getCategoriaIds());
        assertEquals(1, request.getCargos().size());
        assertEquals(new BigDecimal("3000000.00"), request.getCargos().getFirst().getCategoriaBasico());
        assertEquals(9001, request.getCargos().getFirst().getCategoriaId());
        assertEquals("Directora ETEC", request.getCargos().getFirst().getCategoriaNombre());
        assertEquals(1, request.getCargos().getFirst().getJornada());
        assertEquals(0, request.getCargos().getFirst().getPresentismo());
        assertEquals("A", request.getCargos().getFirst().getSituacion());
    }

    @Test
    void fallaSinValorDePunto() {
        when(controlClient.findByPeriodo(2026, 11)).thenReturn(control("0.00"));

        assertThrows(EtecValidationException.class, () -> service.preview(2026, 11));
        verify(cargoLiquidacionClient, never()).replaceByPeriodo(any(), any(), any());
    }

    @Test
    void ignoraAsignacionesDeCategoriasSinPuntos() {
        when(controlClient.findByPeriodo(2026, 11)).thenReturn(control("3000.00"));
        when(categoriaClient.findConPuntos()).thenReturn(List.of(directoraEtec()));
        when(asignacionClient.findVigentesByPeriodo(2026, 11)).thenReturn(List.of(
                asignacion(),
                EtecAsignacionCargoDto.builder().asignacionCargoId(2L).legajoId(999L)
                        .categoriaId(1306).build()));

        var preview = service.preview(2026, 11);

        assertEquals(1, preview.size());
        assertEquals(1234L, preview.getFirst().getLegajoId());
    }

    @Test
    void previewNoEscribeEnCore() {
        when(controlClient.findByPeriodo(2026, 11)).thenReturn(control("3000.00"));
        when(categoriaClient.findConPuntos()).thenReturn(List.of(directoraEtec()));
        when(asignacionClient.findVigentesByPeriodo(2026, 11)).thenReturn(List.of(asignacion()));

        service.preview(2026, 11);

        verify(cargoLiquidacionClient, never()).replaceByPeriodo(any(), any(), any());
        verify(cargoLiquidacionClient, never()).replaceByLegajo(any(), any(), any(), any());
    }
}
