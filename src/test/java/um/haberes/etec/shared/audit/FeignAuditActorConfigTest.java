package um.haberes.etec.shared.audit;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import feign.RequestTemplate;

/**
 * El actor de auditoría no viaja solo: si el interceptor deja de copiar los headers, las
 * escrituras ETEC se auditan sin legajo solicitante. Este test lo fija.
 */
class FeignAuditActorConfigTest {

    @AfterEach
    void limpiarContexto() {
        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    void copiaElActorLegacyYElHeaderNuevoCuandoVienenEnLaRequest() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(FeignAuditActorConfig.HEADER_LEGAJO_LEGACY, "999");
        request.addHeader(FeignAuditActorConfig.HEADER_LEGAJO, "999");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        RequestTemplate template = new RequestTemplate();
        FeignAuditActorConfig.copyAuditActor(FeignAuditActorConfig.currentAttributes(), template);

        assertThat(template.headers())
                .containsEntry(FeignAuditActorConfig.HEADER_LEGAJO_LEGACY, java.util.List.of("999"))
                .containsEntry(FeignAuditActorConfig.HEADER_LEGAJO, java.util.List.of("999"));
    }

    @Test
    void noAgregaHeadersSiLaRequestNoLosTrae() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        RequestTemplate template = new RequestTemplate();
        FeignAuditActorConfig.copyAuditActor(FeignAuditActorConfig.currentAttributes(), template);

        assertThat(template.headers()).isEmpty();
    }

    @Test
    void noFallaNiAgregaNadaSinRequestAsociada() {
        RequestTemplate template = new RequestTemplate();

        FeignAuditActorConfig.copyAuditActor(FeignAuditActorConfig.currentAttributes(), template);

        assertThat(template.headers()).isEmpty();
    }

    @Test
    void respetaUnHeaderYaPresenteEnLaPlantilla() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(FeignAuditActorConfig.HEADER_LEGAJO_LEGACY, "111");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        RequestTemplate template = new RequestTemplate();
        template.header(FeignAuditActorConfig.HEADER_LEGAJO_LEGACY, "222");
        FeignAuditActorConfig.copyAuditActor(FeignAuditActorConfig.currentAttributes(), template);

        assertThat(template.headers())
                .containsEntry(FeignAuditActorConfig.HEADER_LEGAJO_LEGACY, java.util.List.of("222"));
    }
}
