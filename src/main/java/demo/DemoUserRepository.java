package demo;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface DemoUserRepository extends MongoRepository<DemoUser, String> {

    public DemoUser findByUsername(String username);

}
