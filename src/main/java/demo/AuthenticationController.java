package demo;

import org.bson.Document;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

@Controller
public class AuthenticationController {

    @GetMapping("/authenticate")
    public String welcome(Model model) {
        model.addAttribute("authentication", new Authentication());
        return "authenticate";
    }

    @PostMapping("/authenticate")
    public String authenticationSubmit(@ModelAttribute Authentication authentication) {
        MongoCollection<Document> usersCollection = getUsersCollection();

        String username = authentication.getUsername();
        String password = authentication.getPassword();

        String query = "{$where: \"this.username == \\\"" + username + "\\\" && "
            + "this.password == \\\"" + password + "\\\"\" }";

        Document doc = Document.parse(query);

        FindIterable<Document> it = usersCollection.find(doc);
        Document r = it.first();
        if (r != null) {
            return "authenticationsucceeded";
        }
        return "authenticationfailed";
    }

    private MongoCollection<Document> getUsersCollection() {
        MongoClientURI connectionString = new MongoClientURI("mongodb://localhost:27017");
        MongoClient mongoClient = new MongoClient(connectionString);
        MongoDatabase database = mongoClient.getDatabase("voxxed_days_demo_db");
        MongoCollection<Document> collection = database.getCollection("users");
        return collection;
    }

}
