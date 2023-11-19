package msgfindb.msgfinbackend;

import msgfindb.msgfinbackend.entity.User;
import msgfindb.msgfinbackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.List;

@SpringBootApplication
public class MsgFinBackendApplication implements CommandLineRunner {

    @Autowired
    private DataGenerator dataGenerator;

    public static void main(String[] args) {
        SpringApplication.run(MsgFinBackendApplication.class, args);
    }

    @Override
    public void run(String... args) {
        dataGenerator.generateAndSaveData();
    }
}

