package msgfindb.msgfinbackend.controller;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.ui.Model;

import msgfindb.msgfinbackend.entity.User;
import msgfindb.msgfinbackend.service.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {
    UserService userService;
    PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public String login(String username, String password, Model model) {
        // Retrieve the user from the user service
        User user = userService.getUserByUsername(username);

        // Check if the user exists and the password is correct
        if (user != null && passwordEncoder.matches(password, user.getPassword())) {
            model.addAttribute("username", username);
            return "welcome"; // Redirect to a welcome page or any other page
        } else {
            model.addAttribute("error", "Invalid username or password");
            return "login"; // Redirect back to the login page with an error message
        }
    }

    @PostMapping("/register")
    public String register(User user, Model model, String password) {

        // Check if the username is already taken
        if (userService.getUserByUsername(user.getUsername()) != null) {
            model.addAttribute("error", "Username already exists. Please choose another.");
            return "register"; // Redirect back to the registration page with an error message
        }
        // Encode the password before saving it
        user.setPassword(passwordEncoder.encode(password));

        // Save the user
        userService.save(user);

        // Redirect to a success page or login page
        return "redirect:/login";
    }
}
