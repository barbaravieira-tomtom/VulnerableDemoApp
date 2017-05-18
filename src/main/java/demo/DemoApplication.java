package demo;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.HtmlUtils;

@SpringBootApplication
@RestController
@EnableGlobalMethodSecurity
@EnableMongoRepositories
public class DemoApplication {

    private boolean ESCAPING_ON = false;

    @Autowired
    UserInfoRepository allusersinfo;

    @Autowired
    DemoUserRepository users;

    @Autowired
    MemoRepository memos;

    @Autowired
    private MongoDBAuthenticationProvider authenticationProvider;

    private String xssInputData = new String(
        "<p> <input type='button' name='Redirect' class='btn btn-primary' value='Submit' "
            + "onclick='alert(document.cookie);'/></p>");

    @RequestMapping("/user")
    public Principal user(Principal user) {
        return user;
    }

    @Secured("ROLE_USER")
    @RequestMapping(value = "/getallusersinfo", method = RequestMethod.GET)
    public DemoResponse getAllUsersInfo() {
        DemoResponse response = new DemoResponse("Done", allusersinfo.findAll());
        return response;
    }

    @Secured("ROLE_USER")
    @RequestMapping(value = "/postusersinfo", method = RequestMethod.POST)
    public DemoResponse postUsersInfo(@RequestBody UserInfo usersinfo) {
        allusersinfo.save(usersinfo);
        DemoResponse response = new DemoResponse("Done", usersinfo);
        return response;
    }

    @Secured("ROLE_USER")
    @RequestMapping("/resource")
    public Map<String, Object> home() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("id", UUID.randomUUID().toString());
        model.put("content", "Welcome " + auth.getName() + ", you're logged in.");
        return model;
    }

    @RequestMapping(value = "/postxss", method = RequestMethod.POST)
    public DemoResponse postXSS(@RequestBody String xssInput) {
        //xssInputData = new String(xssInput); 
        String freetext = xssInput;
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (isEscapingEnabled())
            freetext = HtmlUtils.htmlEscape(xssInput);

        memos.delete(auth.getName());
        memos.save(new Memo(auth.getName(), freetext));
        DemoResponse response = new DemoResponse("Done", xssInput);
        return response;
    }

    @RequestMapping(value = "/getxss", method = RequestMethod.GET)
    public DemoResponse getxss() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Memo memo = memos.findByIdentifier(auth.getName());
        DemoResponse response = new DemoResponse("Error", "Error");
        if (memo != null)
            response = new DemoResponse("Done", memo.getFreetext());
        return response;
    }

    private boolean isEscapingEnabled() {
        return this.ESCAPING_ON;
    }

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

    //    @RequestMapping("/addUser")
    //    public void addUsers() {
    //        StandardPasswordEncoder encoder = new StandardPasswordEncoder();
    //        users.deleteAll();
    //        users.save(new DemoUser("test", encoder.encode("password"), true, "USER"));
    //        customers.save(new Customer("John", "Smith"));
    //    }

    //    @Autowired
    //    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
    //        auth.inMemoryAuthentication().withUser("test").password("password").roles("USER");
    //    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        /**
        StandardPasswordEncoder encoder = new StandardPasswordEncoder();
        users.deleteAll();
        SimpleGrantedAuthority role = new SimpleGrantedAuthority("ROLE_USER");
        users.save(new DemoUser("test", encoder.encode("password"), true, Arrays.asList(role)));
        **/
        auth.authenticationProvider(authenticationProvider);
    }
}
