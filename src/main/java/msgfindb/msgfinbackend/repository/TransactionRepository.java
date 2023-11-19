package msgfindb.msgfinbackend.repository;

import jakarta.transaction.Transactional;
import msgfindb.msgfinbackend.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long>, JpaSpecificationExecutor<Transaction> {
    // Find all transactions within a specific date range
    List<Transaction> findByDateBetween(LocalDateTime startDate, LocalDateTime endDate);

    // Find all transactions for a specific category
    List<Transaction> findByCategory(String category);


    // Find all transactions for a specific user
    List<Transaction> findByUserId(Long userId);

    // Find a transaction by ID and UserID
    Optional<Transaction> findByIdAndUserId(Long id, Long userId);

    // Check if a transaction exists by ID and UserID
    boolean existsByIdAndUserId(Long id, Long userId);

    @Query("SELECT t FROM Transaction t WHERE t.userId = :userId AND t.date >= :startDate AND t.date <= :endDate")
    List<Transaction> findByUserIdAndDateBetween(@Param("userId") Long userId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);


    // Delete transactions by ID and UserID
    @Modifying
    @Transactional
    @Query("DELETE FROM Transaction t WHERE t.id = :id AND t.userId = :userId")
    void deleteByIdAndUserId(@Param("id") Long id, @Param("userId") Long userId);

    // Delete multiple transactions by ID list and UserID
    @Modifying
    @Transactional
    @Query("DELETE FROM Transaction t WHERE t.id IN :ids AND t.userId = :userId")
    void deleteByIdInAndUserId(@Param("ids") List<Long> ids, @Param("userId") Long userId);

    List<Transaction> findByUserIdAndDateBetween(Long userId, LocalDateTime startDate, LocalDateTime endDate);
}
