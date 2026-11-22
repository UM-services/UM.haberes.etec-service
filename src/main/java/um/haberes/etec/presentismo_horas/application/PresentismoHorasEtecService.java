package um.haberes.etec.presentismo_horas.application;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import um.haberes.etec.client.core.CoreEtecPresentismoHorasClient;
import um.haberes.etec.client.core.dto.EtecPresentismoHorasDto;
import um.haberes.etec.client.core.dto.EtecPresentismoHorasRequestDto;
import um.haberes.etec.client.core.dto.EtecPresentismoHorasUpdateRequestDto;
import um.haberes.etec.presentismo_horas.infrastructure.web.dto.PresentismoHorasRequest;
import um.haberes.etec.presentismo_horas.infrastructure.web.dto.PresentismoHorasUpdateRequest;

/**
 * Proxy de la pérdida de presentismo ETEC por horas. El CRUD y las validaciones viven en
 * haberes-core; etec expone el circuito ETEC al frontend.
 */
@Service
@RequiredArgsConstructor
public class PresentismoHorasEtecService {

    private final CoreEtecPresentismoHorasClient client;

    public EtecPresentismoHorasDto findByLegajoAndPeriodo(Long legajoId, Integer anho, Integer mes) {
        return client.findByLegajoAndPeriodo(legajoId, anho, mes);
    }

    public List<EtecPresentismoHorasDto> findAllByPeriodo(Integer anho, Integer mes) {
        return client.findByPeriodo(anho, mes);
    }

    public EtecPresentismoHorasDto findById(Long presentismoHorasId) {
        return client.findById(presentismoHorasId);
    }

    public EtecPresentismoHorasDto create(PresentismoHorasRequest request) {
        return client.add(EtecPresentismoHorasRequestDto.builder()
                .legajoId(request.getLegajoId())
                .anho(request.getAnho())
                .mes(request.getMes())
                .motivo(request.getMotivo())
                .build());
    }

    public EtecPresentismoHorasDto update(
            Long presentismoHorasId, PresentismoHorasUpdateRequest request) {
        return client.update(EtecPresentismoHorasUpdateRequestDto.builder()
                .motivo(request.getMotivo())
                .build(), presentismoHorasId);
    }

    public void delete(Long presentismoHorasId) {
        client.delete(presentismoHorasId);
    }
}
