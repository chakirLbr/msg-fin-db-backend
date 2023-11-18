package msgfindb.msgfinbackend;

import msgfindb.msgfinbackend.entity.Transaction;
import msgfindb.msgfinbackend.repository.TransactionRepository;
import org.hibernate.annotations.Comment;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Random;

@Component
public class Testdata {

    private TransactionRepository transactionRepository;
    public Testdata(TransactionRepository transactionRepository) {

        this.transactionRepository = transactionRepository;
        createTestdata();
    }

    public void createTestdata() {
        Random random = new Random();
        for (int i = 0; i < 10; i++) {
            Transaction transaction = new Transaction();
            transaction.setTransactionDate(LocalDateTime.now());
            transaction.setAmount(BigDecimal.valueOf(random.nextInt()));
            transaction.setCategory("Miete");
            this.transactionRepository.save(transaction);
        }
    }
}
