package software.plusminus.security.service;

import java.util.Map;
import javax.annotation.Nullable;

public interface SecurityParameterProvider {

    @Nullable
    Map.Entry<String, String> providerParameter();

}
