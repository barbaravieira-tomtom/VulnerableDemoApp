package demo;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.session.SessionManagementFilter;

@Configuration
@EnableWebSecurity
//@Order(SecurityProperties.ACCESS_OVERRIDE_ORDER)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {

        String[] patterns = new String[] {"/index.html", "/home.html", "/login1.html", "/xss2.html", "/", "/xss1.html",
            "/usersinfopage.html", "/error.html", "/js/*.js", "/css/*.css", "/error", "/xss", "/login", "/resource",
            "/postusersinfo", "/getallusersinfo", "/getxss", "/postxss"};

        // @formatter:off
        //http.httpBasic();
        //http.authorizeRequests().antMatchers("/**").permitAll(); //.anyRequest().authenticated();
        //http.csrf().disable();
        //http.httpBasic().and().authorizeRequests().

        http.headers().httpStrictTransportSecurity();
        http.formLogin()
            .loginPage("/login")
            .defaultSuccessUrl("/home.html")
            .failureUrl("/error")
            .and()
            .logout()
            .and()
            .authorizeRequests()
            .antMatchers(patterns)
            .permitAll()
            .anyRequest()

            .authenticated();
        http.csrf().and().addFilterAfter(new CsrfGrantingFilter(), SessionManagementFilter.class);


        //http.csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse());
        // @formatter:on
    }

    protected class CsrfGrantingFilter implements Filter {
        @Override
        public void init(FilterConfig filterConfig) throws ServletException {}

        @Override
        public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
            CsrfToken csrf = (CsrfToken)servletRequest.getAttribute(CsrfToken.class.getName());
            HttpServletResponse response = (HttpServletResponse)servletResponse;
            HttpServletRequest request = (HttpServletRequest)servletRequest;

            System.out.println(request.getHeader("x-requested-with"));
            //if (request.getHeader("x-requested-with")!= null) {
            String token = csrf.getToken();
            if (token != null) {
                Cookie cookie = new Cookie("CSRF-TOKEN", token);
                cookie.setPath("/");
                response.addCookie(cookie);
            }
            filterChain.doFilter(servletRequest, servletResponse);
            //}
            //} else
            //    response.sendError(Response.SC_FORBIDDEN);
        }

        @Override
        public void destroy() {}
    }

    
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}
}