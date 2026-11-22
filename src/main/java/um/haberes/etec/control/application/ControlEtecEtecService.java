package um.haberes.etec.control.application;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import um.haberes.etec.client.core.CoreControlEtecClient;
import um.haberes.etec.client.core.dto.ControlEtecDto;

/**
 * Proxy del control ETEC del período. El dato lo persiste haberes-core; etec lo expone
 * para que el frontend del circuito ETEC no llame directo a core.
 */
@Service
@RequiredArgsConstructor
public class ControlEtecEtecService {

    private final CoreControlEtecClient client;

    public ResponseEntity<ControlEtecDto> findByPeriodoOpcional(Integer anho, Integer mes) {
        return client.findByPeriodoOpcional(anho, mes);
    }
}
