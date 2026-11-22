package um.haberes.etec.presentismo.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import um.haberes.etec.client.core.CoreEtecPresentismoClient;
import um.haberes.etec.client.core.dto.EtecPresentismoDto;
import um.haberes.etec.client.core.dto.EtecPresentismoRequestDto;
import um.haberes.etec.client.core.dto.EtecPresentismoUpdateRequestDto;
import um.haberes.etec.presentismo.infrastructure.web.dto.PresentismoRequest;
import um.haberes.etec.presentismo.infrastructure.web.dto.PresentismoUpdateRequest;

@ExtendWith(MockitoExtension.class)
class PresentismoEtecServiceTest {

    @Mock
    private CoreEtecPresentismoClient client;

    @InjectMocks
    private PresentismoEtecService service;

    @Test
    void delegaLaLecturaPorLegajoYPeriodo() {
        when(client.findByLegajoAndPeriodo(100L, 2026, 11)).thenReturn(List.of(
                EtecPresentismoDto.builder().presentismoId(1L).build()));

        var result = service.findAllByLegajoAndPeriodo(100L, 2026, 11);

        assertEquals(1, result.size());
        verify(client).findByLegajoAndPeriodo(100L, 2026, 11);
    }

    @Test
    void creaMapeandoElRequestDelFrontend() {
        when(client.add(any())).thenReturn(EtecPresentismoDto.builder().presentismoId(7L).build());

        var result = service.create(PresentismoRequest.builder()
                .asignacionCargoId(12L)
                .anho(2026)
                .mes(11)
                .motivo("Ausencia")
                .build());

        ArgumentCaptor<EtecPresentismoRequestDto> captor =
                ArgumentCaptor.forClass(EtecPresentismoRequestDto.class);
        verify(client).add(captor.capture());
        assertEquals(12L, captor.getValue().getAsignacionCargoId());
        assertEquals(2026, captor.getValue().getAnho());
        assertEquals(11, captor.getValue().getMes());
        assertEquals("Ausencia", captor.getValue().getMotivo());
        assertEquals(7L, result.getPresentismoId());
    }

    @Test
    void actualizaSoloElMotivo() {
        when(client.update(any(), eq(7L)))
                .thenReturn(EtecPresentismoDto.builder().presentismoId(7L).build());

        service.update(7L, PresentismoUpdateRequest.builder().motivo("Ausencia").build());

        ArgumentCaptor<EtecPresentismoUpdateRequestDto> captor =
                ArgumentCaptor.forClass(EtecPresentismoUpdateRequestDto.class);
        verify(client).update(captor.capture(), eq(7L));
        assertEquals("Ausencia", captor.getValue().getMotivo());
    }

    @Test
    void eliminaPorId() {
        service.delete(7L);

        verify(client).delete(7L);
    }
}
