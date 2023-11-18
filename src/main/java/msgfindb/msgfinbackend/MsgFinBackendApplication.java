package msgfindb.msgfinbackend;

import msgfindb.msgfinbackend.entity.Transaction;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Random;

@SpringBootApplication
public class MsgFinBackendApplication {


	public static void main(String[] args) {
		SpringApplication.run(MsgFinBackendApplication.class, args);

		Random random = new Random();
		for (int i = 0; i < 10; i++) {
			Transaction transaction = new Transaction();
			transaction.setTransactionDate(LocalDateTime.now());
			transaction.setAmount(BigDecimal.valueOf(random.nextInt()));
		}
	}
}

