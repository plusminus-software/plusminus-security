package software.plusminus.authorization.service.localhost;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import software.plusminus.authorization.model.AuthorizationResult;
import software.plusminus.authorization.service.AnnotationAuthorizer;
import software.plusminus.context.Context;

import javax.servlet.http.HttpServletRequest;

@AllArgsConstructor
@Component
public class LocalhostOnlyAuthorizer implements AnnotationAuthorizer<LocalhostOnly> {

    private Context<HttpServletRequest> httpServletRequestContext;

    @SuppressWarnings("PMD.AvoidUsingHardCodedIP")
    @Override
    public AuthorizationResult authorize(LocalhostOnly annotation) {
        String ip = IpUtils.getClientIpAddress(httpServletRequestContext.get());
        if (!"localhost".equals(ip) && !"127.0.0.1".equals(ip)) {
            return AuthorizationResult.error("Resource is accessible from local network only");
        }
        return AuthorizationResult.ok();
    }
}
