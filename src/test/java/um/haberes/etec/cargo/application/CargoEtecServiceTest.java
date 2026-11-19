package um.haberes.etec.cargo.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import um.haberes.etec.cargo.infrastructure.web.dto.CargoEtecRequest;
import um.haberes.etec.client.core.CoreCategoriaClient;
import um.haberes.etec.client.core.CoreEtecAsignacionCargoClient;
import um.haberes.etec.client.core.dto.CategoriaDto;
import um.haberes.etec.client.core.dto.CategoriaRequestDto;
import um.haberes.etec.shared.exception.EtecConflictException;
import um.haberes.etec.shared.exception.EtecValidationException;

@ExtendWith(MockitoExtension.class)
class CargoEtecServiceTest {

    @Mock
    private CoreCategoriaClient categoriaClient;

    @Mock
    private CoreEtecAsignacionCargoClient asignacionClient;

    @InjectMocks
    private CargoEtecService service;

    @Test
    void creaCategoriaDocenteConBasicoCeroYPuntos() {
        when(categoriaClient.findLast()).thenReturn(CategoriaDto.builder().categoriaId(1306).build());
        when(categoriaClient.add(any(), eq(2026), eq(11)))
                .thenReturn(CategoriaDto.builder().categoriaId(1307).build());

        service.create(2026, 11, CargoEtecRequest.builder()
                .nombre("Directora ETEC").puntos(new BigDecimal("1000.00")).build());

        ArgumentCaptor<CategoriaRequestDto> captor = ArgumentCaptor.forClass(CategoriaRequestDto.class);
        verify(categoriaClient).add(captor.capture(), eq(2026), eq(11));
        CategoriaRequestDto body = captor.getValue();
        assertEquals(1307, body.getCategoriaId());
        assertEquals("Directora ETEC", body.getNombre());
        assertEquals(1, body.getDocente());
        assertEquals(0, body.getNoDocente());
        assertEquals(0, body.getLiquidaPorHora());
        assertEquals(BigDecimal.ZERO, body.getBasico());
        assertEquals(new BigDecimal("1000.00"), body.getPuntos());
    }

    @Test
    void rechazaCategoriaSinPuntos() {
        when(categoriaClient.findById(1306)).thenReturn(CategoriaDto.builder()
                .categoriaId(1306).docente(1).puntos(BigDecimal.ZERO).build());

        assertThrows(EtecValidationException.class, () -> service.findById(1306));
    }

    @Test
    void rechazaCategoriaNoDocente() {
        when(categoriaClient.findById(1306)).thenReturn(CategoriaDto.builder()
                .categoriaId(1306).docente(0).puntos(new BigDecimal("1000")).build());

        assertThrows(EtecValidationException.class, () -> service.findById(1306));
    }

    @Test
    void noBorraUnCargoConAsignaciones() {
        when(categoriaClient.findById(9001)).thenReturn(CategoriaDto.builder()
                .categoriaId(9001).docente(1).puntos(new BigDecimal("1000")).build());
        when(asignacionClient.existsByCategoria(9001)).thenReturn(true);

        assertThrows(EtecConflictException.class, () -> service.delete(9001));
        verify(categoriaClient, never()).delete(9001);
    }
}
