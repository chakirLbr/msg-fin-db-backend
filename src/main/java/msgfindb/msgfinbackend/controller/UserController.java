package msgfindb.msgfinbackend.controller;
import msgfindb.msgfinbackend.entity.Transaction;
import msgfindb.msgfinbackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import msgfindb.msgfinbackend.entity.User;
import msgfindb.msgfinbackend.service.UserService;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;


    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestParam String username, @RequestParam String password) {
        // Retrieve the user from the user service
        User user = userService.getUserByName(username);
        if (user == null) {
            String message = "Username is false or doesn't exist";
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(message);
        } else {
            if (user.getPassword().equals(password)) {
                return new ResponseEntity<>(user, HttpStatus.OK);
            } else {
                String message = "Password is false";
                return ResponseEntity.badRequest().body(message);
            }
        }
    }

    @PostMapping("register")
    public ResponseEntity<Object> register(@RequestParam String username, @RequestParam String password) {
        User user = userService.getUserByUsernameAndPassword(username, password);
        if (user == null){
            User newuser = new User();
            newuser.setUsername(username);
            newuser.setPassword(password);
            userService.save(newuser);
            return new ResponseEntity<>(newuser, HttpStatus.CREATED);
        }else {
            String message = "Username already exists ";
            return ResponseEntity.badRequest().body(message);
        }
     }
    @GetMapping("getAllUsers")
    public ResponseEntity<List<User>> getAllUser() {
        List<User> users = userService.getAllusers();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }
    }
