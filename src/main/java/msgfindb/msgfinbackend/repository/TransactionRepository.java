package msgfindb.msgfinbackend.repository;

import jakarta.transaction.Transactional;
import msgfindb.msgfinbackend.entity.Account;
import msgfindb.msgfinbackend.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    // Find all transactions within a specific date range
    List<Transaction> findByDateBetween(LocalDateTime startDate, LocalDateTime endDate);

    // Find all transactions for a specific category
    List<Transaction> findByCategory(String category);

    // Find all transactions for a specific account
    List<Transaction> findByAccountId(Long accountId);

    // Find all transactions for a specific category and account
    List<Transaction> findByCategoryAndAccountId(String category, Long accountId);

    // Find the total amount of transactions for a specific category
    @Query("SELECT SUM(t.amount) FROM Transaction t WHERE t.category = :category")
    BigDecimal getTotalAmountForCategory(@Param("category") String category);

    // Find the total amount of transactions for a specific account
    @Query("SELECT SUM(t.amount) FROM Transaction t WHERE t.accountId = :accountId")
    BigDecimal getTotalAmountForAccount(@Param("accountId") Long accountId);

    // Find the total amount of transactions for a specific category and account
    @Query("SELECT SUM(t.amount) FROM Transaction t WHERE t.category = :category AND t.accountId = :accountId")
    BigDecimal getTotalAmountForCategoryAndAccount(@Param("category") String category, @Param("accountId") Long accountId);
    // Update transaction description by ID
    @Modifying
    @Transactional
    @Query("UPDATE Transaction t SET t.description = :description WHERE t.id = :id")
    int updateTransactionDescription(@Param("id") Long id, @Param("description") String description);

    // Delete transactions by category
    @Modifying
    @Transactional
    @Query("DELETE FROM Transaction t WHERE t.category = :category")
    void deleteTransactionsByCategory(@Param("category") String category);

    // Delete transactions by account ID
    @Modifying
    @Transactional
    @Query("DELETE FROM Transaction t WHERE t.accountId = :accountId")
    void deleteTransactionsByAccountId(@Param("accountId") Long accountId);
}
