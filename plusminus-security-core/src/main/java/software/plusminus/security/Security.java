package software.plusminus.security;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

@Getter
@Builder
@EqualsAndHashCode
public class Security {

    private String username;
    @Builder.Default
    private Set<String> roles = Collections.emptySet();
    @Builder.Default
    private Map<String, String> parameters = Collections.emptyMap();
    
}
