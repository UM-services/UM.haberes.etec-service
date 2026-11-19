package um.haberes.etec.asignacion_cargo.application;

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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import um.haberes.etec.asignacion_cargo.infrastructure.web.dto.AsignacionCargoRequest;
import um.haberes.etec.cargo.application.CargoEtecService;
import um.haberes.etec.client.core.CoreEtecAsignacionCargoClient;
import um.haberes.etec.client.core.dto.CategoriaDto;
import um.haberes.etec.client.core.dto.EtecAsignacionCargoDto;
import um.haberes.etec.shared.exception.EtecConflictException;
import um.haberes.etec.shared.exception.EtecValidationException;

@ExtendWith(MockitoExtension.class)
class AsignacionCargoServiceTest {

    @Mock
    private CoreEtecAsignacionCargoClient client;

    @Mock
    private CargoEtecService cargoEtecService;

    @InjectMocks
    private AsignacionCargoService service;

    private OffsetDateTime date(int day) {
        return OffsetDateTime.of(2026, 11, day, 0, 0, 0, 0, ZoneOffset.UTC);
    }

    private AsignacionCargoRequest request(Integer dayFrom, Integer dayTo) {
        return AsignacionCargoRequest.builder()
                .legajoId(1234L)
                .categoriaId(9001)
                .dependenciaId(1)
                .fechaAlta(date(dayFrom))
                .fechaBaja(dayTo == null ? null : date(dayTo))
                .build();
    }

    private void categoriaValida() {
        when(cargoEtecService.findById(9001)).thenReturn(CategoriaDto.builder()
                .categoriaId(9001).docente(1).puntos(new BigDecimal("1000")).build());
    }

    @Test
    void rechazaBajaAnteriorAlAlta() {
        categoriaValida();

        assertThrows(EtecValidationException.class,
                () -> service.create(request(15, 10)));
        verify(client, never()).add(any());
    }

    @Test
    void rechazaSolapeDeLaMismaCategoria() {
        categoriaValida();
        when(client.findByLegajo(1234L)).thenReturn(List.of(EtecAsignacionCargoDto.builder()
                .asignacionCargoId(1L)
                .legajoId(1234L)
                .categoriaId(9001)
                .fechaAlta(date(1))
                .fechaBaja(null)
                .build()));

        assertThrows(EtecConflictException.class, () -> service.create(request(15, null)));
        verify(client, never()).add(any());
    }

    @Test
    void permiteCategoriaDistinta() {
        categoriaValida();
        when(client.findByLegajo(1234L)).thenReturn(List.of(EtecAsignacionCargoDto.builder()
                .asignacionCargoId(1L)
                .legajoId(1234L)
                .categoriaId(1306)
                .fechaAlta(date(1))
                .fechaBaja(null)
                .build()));
        when(client.add(any())).thenReturn(EtecAsignacionCargoDto.builder().asignacionCargoId(2L).build());

        var result = service.create(request(15, null));

        assertEquals(2L, result.getAsignacionCargoId());
        verify(client).add(any());
    }

    @Test
    void permiteEditarLaMismaAsignacionSinConsiderarlaSolape() {
        categoriaValida();
        when(client.findByLegajo(1234L)).thenReturn(List.of(EtecAsignacionCargoDto.builder()
                .asignacionCargoId(1L)
                .legajoId(1234L)
                .categoriaId(9001)
                .fechaAlta(date(1))
                .fechaBaja(null)
                .build()));
        when(client.update(any(), eq(1L)))
                .thenReturn(EtecAsignacionCargoDto.builder().asignacionCargoId(1L).build());

        var result = service.update(1L, request(15, null));

        assertEquals(1L, result.getAsignacionCargoId());
        verify(client).update(any(), eq(1L));
    }
}
