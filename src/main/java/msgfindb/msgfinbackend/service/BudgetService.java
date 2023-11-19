package msgfindb.msgfinbackend.service;

import msgfindb.msgfinbackend.entity.Budget;
import msgfindb.msgfinbackend.entity.Transaction;
import msgfindb.msgfinbackend.repository.BudgetRepository;
import msgfindb.msgfinbackend.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class BudgetService {

    @Autowired
    private BudgetRepository budgetRepository;

    @Autowired
    private TransactionRepository transactionRepository;


    // List all budgets for a specific user ID
    public List<Budget> listAllBudgets(Long userId) {
        LocalDate startOfMonth = LocalDate.now().withDayOfMonth(1);
        LocalDate endOfMonth = startOfMonth.plusMonths(1).minusDays(1);
        LocalDateTime startDateTime = startOfMonth.atStartOfDay();
        LocalDateTime endDateTime = endOfMonth.atTime(23, 59, 59);

        List<Transaction> transactions = transactionRepository.findByUserIdAndDateBetween(userId, startDateTime, endDateTime);
        List<Budget> budgets = budgetRepository.findByUserId(userId);

        // Aggregate actual budget from transactions
        Map<String, BigDecimal> aggregatedActualBudget = transactions.stream()
                .collect(Collectors.groupingBy(
                        Transaction::getCategory,
                        Collectors.reducing(BigDecimal.ZERO, Transaction::getAmount, BigDecimal::add)
                ));

        // Create a set of all categories (both from transactions and budgets)
        Set<String> allCategories = new HashSet<>();
        transactions.forEach(t -> allCategories.add(t.getCategory()));
        budgets.forEach(b -> allCategories.add(b.getCategory()));

        // Process each category
        for (String category : allCategories) {
            // Find or create the budget object for each category
            Budget budget = budgets.stream()
                    .filter(b -> b.getCategory().equals(category))
                    .findFirst()
                    .orElse(new Budget());

            // Set or update properties
            budget.setCategory(category);
            budget.setCurrentAmount(aggregatedActualBudget.getOrDefault(category, BigDecimal.ZERO));
            budget.setUserId(userId);

            // Save new budgets to the repository and add to the budgets list if not already present
            if (budget.getId() == null) {
                budgetRepository.save(budget);
                budgets.add(budget);
            }
        }

        return budgets;
    }

    // Create a budget for a specific user
    public Budget createBudgetForUser(Long userId, Budget budget) {
        return budgetRepository.save(budget);
    }

    // Update a budget for a specific user
    public Budget updateBudgetForUser(Long userId, Long id, Budget newBudget) {
        Budget existingBudget = budgetRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new NoSuchElementException("Budget not found with id: " + id + " for User ID: " + userId));
        existingBudget.setCategory(newBudget.getCategory());
        existingBudget.setPlannedAmount(newBudget.getPlannedAmount());
        return budgetRepository.save(existingBudget);
    }

    // Delete a budget for a specific user
    public void deleteBudgetForUser(Long userId, Long id) {
        Budget budget = budgetRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new NoSuchElementException("Budget not found with id: " + id + " for User ID: " + userId));
        budgetRepository.deleteById(budget.getId());
    }

    // Get a budget by ID and user
    public Budget getBudgetByIdAndUser(Long id, Long userId) {
        return budgetRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new NoSuchElementException("Budget not found with id: " + id + " for User ID: " + userId));
    }

    // Method to determine budget adherence level
    public String getBudgetAdherenceLevel(Long userId) {
        List<Budget> budgets = listAllBudgets(userId);
        BigDecimal totalPlanned = BigDecimal.ZERO;
        BigDecimal totalActual = BigDecimal.ZERO;

        for (Budget budget : budgets) {
            BigDecimal plannedAmount = budget.getPlannedAmount() != null ? budget.getPlannedAmount() : BigDecimal.ZERO;
            BigDecimal currentAmount = budget.getCurrentAmount() != null ? budget.getCurrentAmount() : BigDecimal.ZERO;

            totalPlanned = totalPlanned.add(plannedAmount);
            totalActual = totalActual.add(currentAmount);
        }

        if (totalPlanned.compareTo(BigDecimal.ZERO) == 0) {
            return "Undefined"; // or some appropriate handling
        }

        BigDecimal difference = totalPlanned.subtract(totalActual).abs();
        BigDecimal percentageDifference = difference.multiply(new BigDecimal(100)).divide(totalPlanned, 2, RoundingMode.HALF_UP);

        // TODO: ADJUST PERCENTAGES
        if (percentageDifference.compareTo(new BigDecimal(33)) <= 0) {
            return "Gold";
        } else if (percentageDifference.compareTo(new BigDecimal(66)) <= 0) {
            return "Silver";
        } else {
            return "Bronze";
        }
    }


    // Delete all budgets for a specific user
    public void deleteAllBudgetsForUser(Long userId, List<Long> budgetIds) {
        budgetRepository.deleteBudgetsByIdsAndUserId(budgetIds, userId);
    }
}