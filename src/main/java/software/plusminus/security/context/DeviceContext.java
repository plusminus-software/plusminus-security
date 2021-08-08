package software.plusminus.security.context;

import javax.annotation.Nullable;

public interface DeviceContext {
    
    @Nullable
    String currentDevice();
    
}
