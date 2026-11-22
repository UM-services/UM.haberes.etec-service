package um.haberes.etec.presentismo.application;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import um.haberes.etec.client.core.CoreEtecPresentismoClient;
import um.haberes.etec.client.core.dto.EtecPresentismoDto;
import um.haberes.etec.client.core.dto.EtecPresentismoRequestDto;
import um.haberes.etec.client.core.dto.EtecPresentismoUpdateRequestDto;
import um.haberes.etec.presentismo.infrastructure.web.dto.PresentismoRequest;
import um.haberes.etec.presentismo.infrastructure.web.dto.PresentismoUpdateRequest;

/**
 * Proxy de la pérdida de presentismo ETEC por asignación. El CRUD y las validaciones
 * viven en haberes-core; etec expone el circuito ETEC al frontend.
 */
@Service
@RequiredArgsConstructor
public class PresentismoEtecService {

    private final CoreEtecPresentismoClient client;

    public List<EtecPresentismoDto> findAllByLegajoAndPeriodo(Long legajoId, Integer anho, Integer mes) {
        return client.findByLegajoAndPeriodo(legajoId, anho, mes);
    }

    public List<EtecPresentismoDto> findAllByPeriodo(Integer anho, Integer mes) {
        return client.findByPeriodo(anho, mes);
    }

    public EtecPresentismoDto findByAsignacionAndPeriodo(
            Long asignacionCargoId, Integer anho, Integer mes) {
        return client.findByAsignacionAndPeriodo(asignacionCargoId, anho, mes);
    }

    public EtecPresentismoDto findById(Long presentismoId) {
        return client.findById(presentismoId);
    }

    public EtecPresentismoDto create(PresentismoRequest request) {
        return client.add(EtecPresentismoRequestDto.builder()
                .asignacionCargoId(request.getAsignacionCargoId())
                .anho(request.getAnho())
                .mes(request.getMes())
                .motivo(request.getMotivo())
                .build());
    }

    public EtecPresentismoDto update(Long presentismoId, PresentismoUpdateRequest request) {
        return client.update(EtecPresentismoUpdateRequestDto.builder()
                .motivo(request.getMotivo())
                .build(), presentismoId);
    }

    public void delete(Long presentismoId) {
        client.delete(presentismoId);
    }
}
