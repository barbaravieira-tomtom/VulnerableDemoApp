package demo;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "memo")
public class Memo {

    @Id
    private String id;
    private String identifier;
    private String freetext;

    public Memo(String identifier, String freetext) {
        setIdentifier(identifier);
        setFreetext(freetext);
    }

    public String getFreetext() {
        return freetext;
    }

    public void setFreetext(String freetext) {
        this.freetext = freetext;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

}