package msgfindb.msgfinbackend.service;

import jdk.jfr.Category;
import msgfindb.msgfinbackend.entity.Budget;
import msgfindb.msgfinbackend.entity.Transaction;
import msgfindb.msgfinbackend.repository.BudgetRepository;
import msgfindb.msgfinbackend.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

@Service
public class BudgetService {
    private BudgetRepository budgetRepository;
    private TransactionRepository transactionRepository;
    public List<Budget> getAllBudgets() {
        return budgetRepository.findAll();
    }
    // List all budgets for the current user by ID
    public List<Budget> listAllBudgets(Long userId) {
        List<Budget> result = new ArrayList<>();
        List<Transaction> transactions = transactionRepository.findAll();
        List<Budget> budgets = getAllBudgets();
        // Collect all unique categories from BudgetPlanning
        for (Budget budget : budgets) {
            double sum = 0;
            String namecategory = budget.getCategory();
            for (Transaction transaction: transactions ) {
                if (transaction.getCategory().equals(namecategory)){
                    sum += transaction.getAmount().doubleValue();
                }
                budget.setCurrentAmount(BigDecimal.valueOf(sum));
            }
            result.add(budget);
        }
        return result;
    }

    public Budget getBudgetById(Long id) {
        return budgetRepository.findById(id).orElseThrow(() -> new NoSuchElementException("Budget not found with id: " + id));
    }

    public Budget updateBudget(Long id, Budget newBudget) {
        Optional<Budget> optionalBudget = budgetRepository.findById(id);

        if (optionalBudget.isPresent()) {
            Budget existingBudget = optionalBudget.get();
            existingBudget.setCategory(newBudget.getCategory());
            existingBudget.setCurrentAmount(newBudget.getCurrentAmount());
            existingBudget.setPlannedAmount(newBudget.getPlannedAmount());
            return budgetRepository.save(existingBudget);
        }

        return null;
    }
    public int updatePlannedAmount(Long id, BigDecimal newPlannedAmount) {
        return budgetRepository.updatePlannedAmountById(id, newPlannedAmount);
    }


    public void deleteBudget(Long id) {
        if (budgetRepository.existsById(id)) {
            budgetRepository.deleteById(id);
        } else {
            throw new NoSuchElementException("Budget not found with id: " + id);
        }
    }

    // Add Budget
    public Budget createBudget(Budget budget) {
        return budgetRepository.save(budget);
    }


}
