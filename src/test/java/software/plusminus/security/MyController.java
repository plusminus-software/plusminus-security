package software.plusminus.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import javax.annotation.security.RolesAllowed;

@SuppressWarnings("OneTopLevelClass")
@RestController
public class MyController {

    @Autowired
    private MyEntityRepository repository;

    @GetMapping("/my-controller")
    @RolesAllowed("admin")
    public List<MyEntity> read() {
        return repository.findAll(Pageable.unpaged()).getContent();
    }

}
