package msgfindb.msgfinbackend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;



@Entity
@Getter
@Setter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String firstname;
    private String surname;
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = true)
    private AccessRights rights = AccessRights.USER; // Default value set here


    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;


    public UserData toUserData() {
        UserData userData = new UserData();
        userData.setId(this.id);
        userData.setFirstname(this.firstname);
        userData.setSurname(this.surname);
        userData.setEmail(this.email);
        userData.setRights(this.rights);
        userData.setUsername(this.username);
        // Exclude the password field
        return userData;
    }

}
