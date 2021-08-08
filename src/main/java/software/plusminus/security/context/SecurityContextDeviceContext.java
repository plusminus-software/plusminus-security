package software.plusminus.security.context;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Nullable;

@Component
public class SecurityContextDeviceContext implements DeviceContext {
    
    @Autowired
    private SecurityContext securityContext;

    @Nullable
    @Override
    public String currentDevice() {
        Object device = securityContext.getOtherParameters().get("device");
        if (device == null) {
            return null;
        }
        return device.toString();
    }
}
