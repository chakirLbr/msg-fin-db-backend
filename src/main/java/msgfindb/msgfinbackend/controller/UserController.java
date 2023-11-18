package msgfindb.msgfinbackend.controller;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import msgfindb.msgfinbackend.entity.User;
import msgfindb.msgfinbackend.service.UserService;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class UserController {
    UserService userService;

    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestParam String username, @RequestParam String password) {
        // Retrieve the user from the user service
        User user = userService.getUserByUsernameAndPassword(username, password);
        if (user == null) {
            String message = "Username is false";
            return ResponseEntity.badRequest().body(message);
        }else {
            if (user.getPassword().equals(password)){
                return new ResponseEntity<>(user, HttpStatus.OK);
            }
            else {
                String message = "Password is false";
                return ResponseEntity.badRequest().body(message);
            }
        }
    }
    @PostMapping("/register")
    public ResponseEntity<Object> register(String username, String password) {
        User user = userService.getUserByUsernameAndPassword(username, password);
        if (user == null){
            User newuser = new User();
            newuser.setUsername(username);
            newuser.setPassword(password);
            return new ResponseEntity<>(user, HttpStatus.OK);
        }else {
            String message = "Username already exists ";
            return ResponseEntity.badRequest().body(message);
        }
     }
    }
