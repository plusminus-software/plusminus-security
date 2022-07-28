package software.plusminus.security;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import software.plusminus.authentication.annotation.Public;
import software.plusminus.security.context.SecurityContext;

import java.util.List;
import javax.annotation.security.RolesAllowed;

@SuppressWarnings("OneTopLevelClass")
@RestController
public class MyController {

    @Autowired
    private SecurityContext securityContext;
    @Autowired
    private MyEntityRepository repository;

    @GetMapping("/my-controller")
    @RolesAllowed("admin")
    public List<MyEntity> read() {
        return repository.findAll(Pageable.unpaged()).getContent();
    }

    @SuppressFBWarnings("ENTITY_MASS_ASSIGNMENT")
    @PostMapping("/my-controller")
    @RolesAllowed("admin")
    public MyEntity create(@RequestBody MyEntity entity) {
        return repository.save(entity);
    }

    @GetMapping("/opened")
    public String opened() {
        return "opened";
    }
    
    @Public
    @GetMapping("/security-context")
    public Security securityContext() {
        return Security.builder()
                .username(securityContext.getUsername())
                .roles(securityContext.getRoles())
                .others(securityContext.getOthers())
                .build();
    }
}
