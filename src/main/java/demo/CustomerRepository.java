package demo;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface CustomerRepository extends MongoRepository<Customer, String> {

    public Customer findByFirstname(String firstName);

    public List<Customer> findByLastname(String lastName);

}
