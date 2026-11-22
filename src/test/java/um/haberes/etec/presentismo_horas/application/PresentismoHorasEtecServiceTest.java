package um.haberes.etec.presentismo_horas.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import um.haberes.etec.client.core.CoreEtecPresentismoHorasClient;
import um.haberes.etec.client.core.dto.EtecPresentismoHorasDto;
import um.haberes.etec.client.core.dto.EtecPresentismoHorasRequestDto;
import um.haberes.etec.client.core.dto.EtecPresentismoHorasUpdateRequestDto;
import um.haberes.etec.presentismo_horas.infrastructure.web.dto.PresentismoHorasRequest;
import um.haberes.etec.presentismo_horas.infrastructure.web.dto.PresentismoHorasUpdateRequest;

@ExtendWith(MockitoExtension.class)
class PresentismoHorasEtecServiceTest {

    @Mock
    private CoreEtecPresentismoHorasClient client;

    @InjectMocks
    private PresentismoHorasEtecService service;

    @Test
    void delegaLaLecturaDelEstadoDelLegajo() {
        when(client.findByLegajoAndPeriodo(100L, 2026, 11)).thenReturn(
                EtecPresentismoHorasDto.builder().legajoId(100L).tieneCargosHoras(true).build());

        var result = service.findByLegajoAndPeriodo(100L, 2026, 11);

        assertEquals(100L, result.getLegajoId());
        assertEquals(true, result.isTieneCargosHoras());
    }

    @Test
    void creaMapeandoElRequestDelFrontend() {
        when(client.add(any())).thenReturn(
                EtecPresentismoHorasDto.builder().presentismoHorasId(7L).build());

        var result = service.create(PresentismoHorasRequest.builder()
                .legajoId(100L)
                .anho(2026)
                .mes(11)
                .motivo("Ausencia")
                .build());

        ArgumentCaptor<EtecPresentismoHorasRequestDto> captor =
                ArgumentCaptor.forClass(EtecPresentismoHorasRequestDto.class);
        verify(client).add(captor.capture());
        assertEquals(100L, captor.getValue().getLegajoId());
        assertEquals(2026, captor.getValue().getAnho());
        assertEquals(11, captor.getValue().getMes());
        assertEquals("Ausencia", captor.getValue().getMotivo());
        assertEquals(7L, result.getPresentismoHorasId());
    }

    @Test
    void actualizaSoloElMotivo() {
        when(client.update(any(), eq(7L)))
                .thenReturn(EtecPresentismoHorasDto.builder().presentismoHorasId(7L).build());

        service.update(7L, PresentismoHorasUpdateRequest.builder().motivo("Ausencia").build());

        ArgumentCaptor<EtecPresentismoHorasUpdateRequestDto> captor =
                ArgumentCaptor.forClass(EtecPresentismoHorasUpdateRequestDto.class);
        verify(client).update(captor.capture(), eq(7L));
        assertEquals("Ausencia", captor.getValue().getMotivo());
    }

    @Test
    void eliminaPorId() {
        service.delete(7L);

        verify(client).delete(7L);
    }
}
