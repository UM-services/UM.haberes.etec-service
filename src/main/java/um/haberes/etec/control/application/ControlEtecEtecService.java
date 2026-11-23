package um.haberes.etec.control.application;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import um.haberes.etec.client.core.CoreControlEtecClient;
import um.haberes.etec.client.core.dto.ControlEtecDto;

/**
 * Proxy del control ETEC del período. El dato lo persiste haberes-core; etec lo expone
 * para que el frontend del circuito ETEC no llame directo a core.
 *
 * <p>Devuelve sólo el cuerpo del control y no el {@code ResponseEntity} de Feign: ese
 * objeto trae los headers del upstream ({@code Transfer-Encoding}, {@code Connection},
 * {@code Keep-Alive}) y, al escribirlos junto con los del servicio, el gateway rechaza la
 * respuesta con {@code 500}.</p>
 */
@Service
@RequiredArgsConstructor
public class ControlEtecEtecService {

    private final CoreControlEtecClient client;

    /** Control del período, o {@code null} si el período todavía no lo tiene. */
    public ControlEtecDto findByPeriodoOpcional(Integer anho, Integer mes) {
        return client.findByPeriodoOpcional(anho, mes).getBody();
    }
}
