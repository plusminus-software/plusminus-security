package software.plusminus.security.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import software.plusminus.authentication.AuthenticationParameters;
import software.plusminus.security.model.User;

import java.util.HashMap;
import javax.servlet.http.HttpServletRequest;

@Service
public class AuthenticationParametersService {
    
    @Autowired
    private HttpServletRequest request;
    
    public AuthenticationParameters createParameters(User user) {
        HashMap<String, Object> otherParameters = new HashMap<>();
        otherParameters.put("tenant", user.getTenant());
        otherParameters.put("email", user.getEmail());
        if (RequestContextHolder.getRequestAttributes() != null) {
            String device = request.getHeader("Device");
            if (device != null) {
                otherParameters.put("device", device);
            }
        }
        return AuthenticationParameters.builder()
                .username(user.getUsername())
                .roles(user.getRoles())
                .otherParameters(otherParameters)
                .build();
    }
}
