import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.security.crypto.bcrypt.BCrypt;

public class User {

    private String username;
    private String passwordHash;
    private String status;

    public User (String username, String password) {
        this.username = username;
        this.passwordHash = BCrypt.hashpw(password, BCrypt.gensalt()); //Hash the password
        this.status = "online";
    }

    public User() {

    }

    public boolean checkPassword (String password) {
        return BCrypt.checkpw(password, passwordHash); //Check the hashed password
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUsername() {
        return username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    @JsonIgnore
    public String getStatus() {
        return status;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

}
