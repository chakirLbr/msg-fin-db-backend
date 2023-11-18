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
    private String name;
    private BigDecimal amount;
    private LocalDateTime date;
    private String category;
    private Long accountId;

}

