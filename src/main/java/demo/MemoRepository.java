package demo;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface MemoRepository extends MongoRepository<Memo, String> {

    public Memo findByIdentifier(String identifier);

}
