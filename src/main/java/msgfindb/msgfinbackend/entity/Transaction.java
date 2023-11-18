package msgfindb.msgfinbackend.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "FIND_TRANSACTION")
@Getter
@Setter
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String description;
    private BigDecimal amount;
    private LocalDateTime transactionDate;


    // Other fields as needed
    private String category;

    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;
}

