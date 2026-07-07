package software.plusminus.authorization.service.localhost;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import software.plusminus.authorization.model.AuthorizationResult;
import software.plusminus.authorization.service.AnnotationAuthorizer;
import software.plusminus.context.Context;

import java.util.Arrays;
import java.util.List;
import javax.servlet.http.HttpServletRequest;

@SuppressWarnings("PMD.AvoidUsingHardCodedIP")
@AllArgsConstructor
@Component
public class LocalhostOnlyAuthorizer implements AnnotationAuthorizer<LocalhostOnly> {

    private static final List<String> LOCALHOST_ADDRESSES = Arrays.asList(
            "localhost", "127.0.0.1", "0:0:0:0:0:0:0:1", "::1");

    private Context<HttpServletRequest> httpServletRequestContext;

    @Override
    public AuthorizationResult authorize(LocalhostOnly annotation) {
        HttpServletRequest request = httpServletRequestContext.get();
        String ip = request == null ? null : request.getRemoteAddr();
        if (!LOCALHOST_ADDRESSES.contains(ip)) {
            return AuthorizationResult.error("Resource is accessible from local network only");
        }
        return AuthorizationResult.ok();
    }
}
