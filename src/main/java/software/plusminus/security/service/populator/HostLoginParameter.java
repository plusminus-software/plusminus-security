package software.plusminus.security.service.populator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.AbstractMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;

@Component
public class HostLoginParameter implements LoginParameter {

    @Autowired
    private HttpServletRequest request;
    
    @Override
    @Nullable
    public Map.Entry<String, Object> authenticationParameter() {
        String host = getHost();
        if (host == null) {
            return null;
        }
        return new AbstractMap.SimpleEntry<>("host", host);
    }

    private String getHost() {
        URL url;
        try {
            url = new URL(request.getRequestURL().toString());
        } catch (MalformedURLException e) {
            throw new SecurityException(e);
        }
        return url.getHost();
    }
}
