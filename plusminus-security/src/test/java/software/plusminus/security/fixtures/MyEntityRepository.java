package software.plusminus.security.fixtures;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository
public interface MyEntityRepository extends PagingAndSortingRepository<MyEntity, Long> {

    void deleteAll();

    @Modifying
    @Query(value = "ALTER TABLE my_entity ALTER COLUMN id RESTART WITH 1", nativeQuery = true)
    //@Query(value = "ALTER TABLE my_entity AUTO_INCREMENT = 1", nativeQuery = true)
    @Transactional
    void resetAutoIncrement();
}
