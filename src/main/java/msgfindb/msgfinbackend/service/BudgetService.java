package msgfindb.msgfinbackend.service;

import jdk.jfr.Category;
import msgfindb.msgfinbackend.entity.Budget;
import msgfindb.msgfinbackend.entity.Transaction;
import msgfindb.msgfinbackend.repository.BudgetRepository;
import msgfindb.msgfinbackend.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

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










    // Add Budget
    public Budget createBudget(Budget budget) {
        return budgetRepository.save(budget);
    }


}
