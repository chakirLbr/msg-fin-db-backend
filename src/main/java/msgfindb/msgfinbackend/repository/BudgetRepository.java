package msgfindb.msgfinbackend.repository;

import jakarta.transaction.Transactional;
import msgfindb.msgfinbackend.entity.Budget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface BudgetRepository extends JpaRepository<Budget, Long> {

    List<Budget> findByCategory(String category);

    // Custom query method to find budgets with current amount greater than a specified value
    List<Budget> findByCurrentAmountGreaterThan(BigDecimal amount);

    // Custom query method to find budgets with planned amount less than a specified value
    List<Budget> findByPlannedAmountLessThan(BigDecimal amount);

    // Custom query method to update the category of a budget by its ID
    @Modifying
    @Transactional
    @Query("UPDATE Budget b SET b.category = :newCategory WHERE b.id = :budgetId")
    int updateCategoryById(@Param("budgetId") Long budgetId, @Param("newCategory") String newCategory);

    // Custom query method to increase the current amount of a budget by its ID
    @Modifying
    @Transactional
    @Query("UPDATE Budget b SET b.currentAmount = b.currentAmount + :amountToAdd WHERE b.id = :budgetId")
    int increaseCurrentAmountById(@Param("budgetId") Long budgetId, @Param("amountToAdd") BigDecimal amountToAdd);
    // Custom query method to update the planned amount of a budget by its ID
    @Modifying
    @Transactional
    @Query("UPDATE Budget b SET b.plannedAmount = :newPlannedAmount WHERE b.id = :budgetId")
    int updatePlannedAmountById(@Param("budgetId") Long budgetId, @Param("newPlannedAmount") BigDecimal newPlannedAmount);
    // Custom query method to delete a budget by its ID
    void deleteById(Long id);

    @Query(value = "SELECT b.*, COALESCE(SUM(t.amount), 0) AS current_amount " +
            "FROM Budget b " +
            "LEFT JOIN Transaction t ON b.category = t.category " +
            "WHERE b.user_id = :userId " +
            "GROUP BY b.id, b.category, b.current_amount", nativeQuery = true)
    List<Budget> listAllBudgets(@Param("userId") Long userId);

}
