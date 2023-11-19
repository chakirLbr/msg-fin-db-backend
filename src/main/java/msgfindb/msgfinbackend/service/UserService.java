package msgfindb.msgfinbackend.service;

import msgfindb.msgfinbackend.entity.User;
import msgfindb.msgfinbackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Optional<User> getUserByName(String username) {
        return userRepository.findByUsername(username.toLowerCase());
    }

    public boolean verifyUserPassword(String username, String password) {
        Optional<User> user = getUserByName(username.toLowerCase());
        return user.filter(value -> passwordEncoder.matches(password, value.getPassword())).isPresent();
    }

    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username.toLowerCase());
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User save(User user) {
        user.setUsername(user.getUsername().toLowerCase());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public List<User> getAllusers() {
        return userRepository.findAll();
    }

}
