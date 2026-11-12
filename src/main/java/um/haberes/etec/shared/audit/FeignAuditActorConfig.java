package um.haberes.etec.shared.audit;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import jakarta.servlet.http.HttpServletRequest;

/**
 * Propaga el actor de auditoría de la request entrante a las llamadas Feign hacia
 * haberes-core.
 *
 * <p>Las escrituras de ETEC se delegan en core ({@code etec_asignacion_cargo},
 * {@code categoria}, {@code cargo_liquidacion}), y el actor de la auditoría lo resuelve
 * el {@code AuditRequestFilter} de core a partir de {@code X-Usuario-Legajo} o
 * {@code X-Legajo-Solicitante}. Feign no reenvía cabeceras por defecto, así que sin este
 * interceptor las altas y bajas quedaban auditadas sin actor.
 *
 * <p>Sólo copia los headers si vienen en la request entrante; en hilos sin request
 * (por ejemplo tareas de fondo) no agrega nada.
 */
@Configuration
public class FeignAuditActorConfig {

    public static final String HEADER_LEGAJO = "X-Usuario-Legajo";

    public static final String HEADER_LEGAJO_LEGACY = "X-Legajo-Solicitante";

    @Bean
    public RequestInterceptor auditActorRequestInterceptor() {
        return template -> copyAuditActor(currentAttributes(), template);
    }

    static void copyAuditActor(ServletRequestAttributes attributes, RequestTemplate template) {
        if (attributes == null) {
            return;
        }
        HttpServletRequest request = attributes.getRequest();
        applyHeader(template, HEADER_LEGAJO, request.getHeader(HEADER_LEGAJO));
        applyHeader(template, HEADER_LEGAJO_LEGACY, request.getHeader(HEADER_LEGAJO_LEGACY));
    }

    static ServletRequestAttributes currentAttributes() {
        return RequestContextHolder.getRequestAttributes() instanceof ServletRequestAttributes servletAttributes
                ? servletAttributes
                : null;
    }

    private static void applyHeader(RequestTemplate template, String name, String value) {
        if (value == null || value.isBlank()) {
            return;
        }
        if (template.headers().containsKey(name)) {
            return;
        }
        template.header(name, value);
    }
}
