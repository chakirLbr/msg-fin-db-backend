package msgfindb.msgfinbackend.service;

import msgfindb.msgfinbackend.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import msgfindb.msgfinbackend.entity.User;


public class UserService {
    UserRepository userRepository;
    PasswordEncoder passwordEncoder;


    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User save(User user) {
        user.setPassword(this.passwordEncoder.encode(user.getPassword()));
        return this.userRepository.save(user);
    }
}
