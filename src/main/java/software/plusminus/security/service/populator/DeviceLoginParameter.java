package software.plusminus.security.service.populator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.util.AbstractMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;

@Component
public class DeviceLoginParameter implements LoginParameter {

    @Autowired
    private HttpServletRequest request;
    
    @Override
    @Nullable
    public Map.Entry<String, Object> authenticationParameter() {
        String device = request.getParameter("device");
        if (device == null) {
            return null;
        }
        return new AbstractMap.SimpleEntry<>("device", device);
    }
}
