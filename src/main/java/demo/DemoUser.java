package demo;

import java.util.Collection;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

@Document(collection = "users")
public class DemoUser {
    @Id
    private String id;
    private String username;
    private String passwordhash;
    private boolean enabled;
    private Collection<SimpleGrantedAuthority> roles;

    public DemoUser(String username, String passwordhash, boolean enabled, Collection<SimpleGrantedAuthority> roles) {
        this.setUsername(username);
        this.setPasswordhash(passwordhash);
        this.setEnabled(enabled);
        this.setRoles(roles);
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordhash() {
        return passwordhash;
    }

    public void setPasswordhash(String passwordhash) {
        this.passwordhash = passwordhash;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Collection<SimpleGrantedAuthority> getRoles() {
        return roles;
    }

    public void setRoles(Collection<SimpleGrantedAuthority> roles) {
        this.roles = roles;
    }

    public String toString() {
        return String.format("User[id=%s, username='%s', passwordhash='%s']", id, username, passwordhash);

    }
}
