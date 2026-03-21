package software.plusminus.user.repository;

import org.springframework.data.repository.Repository;
import software.plusminus.user.model.User;

public interface UserRepository extends Repository<User, Long> {

    User findByEmail(String email);

    User save(User entity);

}
