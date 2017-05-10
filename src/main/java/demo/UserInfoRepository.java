package demo;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserInfoRepository extends MongoRepository<UserInfo, String> {

    public UserInfo findByFirstname(String firstName);

    public List<UserInfo> findByLastname(String lastName);

}
