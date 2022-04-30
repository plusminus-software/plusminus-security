package software.plusminus.security.service;

import org.hibernate.Filter;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import software.plusminus.authentication.AuthenticationParameters;
import software.plusminus.security.model.User;
import software.plusminus.security.service.populator.LoginParameter;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Service
public class LoginService {

    @Autowired
    private UserService userService;
    @Autowired
    private List<LoginParameter> loginParameters;
    @PersistenceContext
    private EntityManager entityManager;

    @SuppressWarnings("PMD.CloseResource")
    @Nullable
    public AuthenticationParameters login(String username, String password, @Nullable String tenant) {
        if (tenant == null) {
            tenant = "";
        }

        User user;
        Session session = entityManager.unwrap(Session.class);
        try {
            Filter filter = session.enableFilter("tenantFilter");
            filter.setParameter("tenant", tenant);
            user = userService.findUser(username, password);
        } finally {
            session.disableFilter("tenantFilter");
        }

        if (user == null) {
            return null;
        }
        Map<String, Object> otherParameters = getOtherParameters(user);
        return AuthenticationParameters.builder()
                .username(user.getUsername())
                .roles(user.getRoles())
                .otherParameters(otherParameters)
                .build();
    }
    
    private Map<String, Object> getOtherParameters(User user) {
        Map<String, Object> otherParameters = new LinkedHashMap<>();
        otherParameters.put("tenant", user.getTenant());
        otherParameters.put("email", user.getEmail());
        loginParameters.stream()
                .map(LoginParameter::authenticationParameter)
                .filter(Objects::nonNull)
                .forEach(e -> otherParameters.put(e.getKey(), e.getValue()));
        return otherParameters;
    }
}
