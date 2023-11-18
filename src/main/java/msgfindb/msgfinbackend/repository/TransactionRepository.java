package msgfindb.msgfinbackend.repository;

import msgfindb.msgfinbackend.entity.Account;
import msgfindb.msgfinbackend.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    // Find all transactions within a specific date range
    List<Transaction> findByTransactionDateBetween(LocalDateTime startDate, LocalDateTime endDate);

    // Find all transactions for a specific category

    // Find all transactions for a specific account
    List<Transaction> findByAccount(Account account);

    // Find all transactions for a specific category and account
    List<Transaction> findByCategoryAndAccount(String category, Account account);

    // Find the total amount of transactions for a specific category
    @Query("SELECT SUM(t.amount) FROM Transaction t WHERE t.category = :category")
    BigDecimal getTotalAmountForCategory(@Param("category") String category);

    // Find the total amount of transactions for a specific account
    @Query("SELECT SUM(t.amount) FROM Transaction t WHERE t.account = :account")
    BigDecimal getTotalAmountForAccount(@Param("account") Account account);

    // Find the total amount of transactions for a specific category and account
    @Query("SELECT SUM(t.amount) FROM Transaction t WHERE t.category = :category AND t.account = :account")
    BigDecimal getTotalAmountForCategoryAndAccount(@Param("category") String category, @Param("account") Account account);
}
