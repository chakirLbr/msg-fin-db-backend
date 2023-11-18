package msgfindb.msgfinbackend.service;

import msgfindb.msgfinbackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import msgfindb.msgfinbackend.entity.User;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;


    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getUserByUsernameAndPassword(String username, String password) {
        return userRepository.findByUsernameAndPassword(username, password);
    }
    public User getUserByName(String username){
        return userRepository.findByUsername(username);
    }
    public List<User> getAllusers(){
        return userRepository.findAll();
    }

    public void save(User user){
        userRepository.save(user);
    }
}
