package msgfindb.msgfinbackend.repository;

import jakarta.transaction.Transactional;
import msgfindb.msgfinbackend.entity.Budget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BudgetRepository extends JpaRepository<Budget, Long> {
    // Custom query method to delete a budget by its ID
    void deleteById(Long id);

    // Find budgets by user ID
    List<Budget> findByUserId(Long userId);

    // Find a budget by ID and user ID
    Optional<Budget> findByIdAndUserId(Long id, Long userId);

    // Custom query method to delete a budget by its ID and User ID
    @Modifying
    @Transactional
    @Query("DELETE FROM Budget b WHERE b.id = :id AND b.userId = :userId")
    void deleteByIdAndUserId(@Param("id") Long id, @Param("userId") Long userId);

    // Custom query method to delete multiple budgets by ID list and UserID
    @Modifying
    @Transactional
    @Query("DELETE FROM Budget b WHERE b.id IN :ids AND b.userId = :userId")
    void deleteBudgetsByIdsAndUserId(@Param("ids") List<Long> ids, @Param("userId") Long userId);

}
