package demo;

import org.springframework.data.annotation.Id;

public class UserInfo {
    @Id
    private String id;
    private String firstname;
    private String lastname;

    public UserInfo() {}

    public UserInfo(String firstname, String lastname) {
        this.firstname = firstname;
        this.lastname = lastname;
    }

    // firstname
    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    // lastname
    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    @Override
    public String toString() {
        return String.format("Customer[id=%s, firstName='%s', lastName='%s']", id, firstname, lastname);
    }
}
