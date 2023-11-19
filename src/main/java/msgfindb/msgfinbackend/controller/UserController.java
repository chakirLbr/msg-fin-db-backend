package msgfindb.msgfinbackend.controller;

import msgfindb.msgfinbackend.ErrorResponse;
import msgfindb.msgfinbackend.entity.User;
import msgfindb.msgfinbackend.entity.UserData;
import msgfindb.msgfinbackend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody User loginUser) {
        if (!userService.existsByUsername(loginUser.getUsername())) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse("Username does not exist", HttpStatus.NOT_FOUND.value()));
        }

        if (userService.verifyUserPassword(loginUser.getUsername(), loginUser.getPassword())) {
            User user = userService.getUserByName(loginUser.getUsername()).orElse(null);
            if (user != null) {
                return ResponseEntity.ok().body(user.toUserData());
            }
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse("Username or password is incorrect", HttpStatus.UNAUTHORIZED.value()));
    }


    @PostMapping("/register")
    public ResponseEntity<Object> register(@RequestBody User newUser) {
        if (userService.existsByUsername(newUser.getUsername())) {
            return ResponseEntity.badRequest().body("Username already exists");
        }
        userService.save(newUser);
        return new ResponseEntity<>(newUser.toUserData(), HttpStatus.CREATED);
    }

    @GetMapping("getAllUsers")
    public ResponseEntity<List<UserData>> getAllUser() {
        List<UserData> usersData = userService.getAllusers().stream().map(User::toUserData).collect(Collectors.toList());
        return new ResponseEntity<>(usersData, HttpStatus.OK);
    }
}
