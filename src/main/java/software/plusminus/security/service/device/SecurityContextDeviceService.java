package software.plusminus.security.service.device;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.plusminus.security.service.SecurityContext;

import javax.annotation.Nullable;

@Service
public class SecurityContextDeviceService implements DeviceService {
    
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
