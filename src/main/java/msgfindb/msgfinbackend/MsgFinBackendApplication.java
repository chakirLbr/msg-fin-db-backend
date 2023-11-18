package msgfindb.msgfinbackend;

import com.mysql.cj.MysqlConnection;
import com.mysql.cj.jdbc.integration.c3p0.MysqlConnectionTester;
import msgfindb.msgfinbackend.entity.Transaction;
import msgfindb.msgfinbackend.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Random;
@SpringBootApplication
public class MsgFinBackendApplication implements CommandLineRunner {

	@Autowired
	private Testdata testdata;

	public static void main(String[] args) {
		SpringApplication.run(MsgFinBackendApplication.class, args);
	}

	@Override
	public void run(String... args) {

		testdata.createTestdata();
	}
}

