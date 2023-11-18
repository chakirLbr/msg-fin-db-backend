package msgfindb.msgfinbackend.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import lombok.Data;
import org.springframework.security.core.userdetails.UserDetails;

@Entity
@Data
@Setter
@Getter

public class User {
    @jakarta.persistence.Id
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String username;
    private String password;

    //private LocalDateTime lastUpdated;


    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }
}