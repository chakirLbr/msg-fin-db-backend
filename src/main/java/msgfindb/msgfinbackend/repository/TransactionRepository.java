package msgfindb.msgfinbackend.repository;

import msgfindb.msgfinbackend.entity.Account;
import msgfindb.msgfinbackend.entity.Category;
import msgfindb.msgfinbackend.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    // Find all transactions within a specific date range
    List<Transaction> findByTransactionDateBetween(LocalDateTime startDate, LocalDateTime endDate);

    // Find all transactions for a specific category
    List<Transaction> findByCategory(Category category);

    // Find all transactions for a specific account
    List<Transaction> findByAccount(Account account);

    // Find all transactions for a specific category and account
    List<Transaction> findByCategoryAndAccount(Category category, Account account);

    // Find the total amount of transactions for a specific category
    @Query("SELECT SUM(t.amount) FROM Transaction t WHERE t.category = :category")
    BigDecimal getTotalAmountForCategory(@Param("category") Category category);

    // Find the total amount of transactions for a specific account
    @Query("SELECT SUM(t.amount) FROM Transaction t WHERE t.account = :account")
    BigDecimal getTotalAmountForAccount(@Param("account") Account account);

    // Find the total amount of transactions for a specific category and account
    @Query("SELECT SUM(t.amount) FROM Transaction t WHERE t.category = :category AND t.account = :account")
    BigDecimal getTotalAmountForCategoryAndAccount(@Param("category") Category category, @Param("account") Account account);
}
