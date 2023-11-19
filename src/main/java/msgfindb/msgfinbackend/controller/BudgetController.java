package msgfindb.msgfinbackend.controller;


import msgfindb.msgfinbackend.ErrorResponse;
import msgfindb.msgfinbackend.entity.Budget;
import msgfindb.msgfinbackend.service.BudgetService;
import msgfindb.msgfinbackend.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.*;


@RestController
@RequestMapping("/api/budget")
public class BudgetController {

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private BudgetService budgetService;

    // List all budgets of a specific user
    @GetMapping("/list/{userId}")
    public ResponseEntity<Object> listUserBudgets(@PathVariable Long userId) {
        try {
            List<Budget> budgets = budgetService.listAllBudgets(userId);
            return new ResponseEntity<>(budgets, HttpStatus.OK);
        } catch (Exception e) {
            ErrorResponse errorResponse = new ErrorResponse("Error listing budgets: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Get a single budget by ID and User
    @GetMapping("/getBudgetById/{userId}/{id}")
    public ResponseEntity<Object> getBudgetById(@PathVariable Long userId, @PathVariable Long id) {
        try {
            Budget budget = budgetService.getBudgetByIdAndUser(id, userId);
            return new ResponseEntity<>(budget, HttpStatus.OK);
        } catch (NoSuchElementException e) {
            ErrorResponse errorResponse = new ErrorResponse("Budget not found with ID: " + id + " for User ID: " + userId, HttpStatus.NOT_FOUND.value());
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            ErrorResponse errorResponse = new ErrorResponse("Error retrieving budget: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Create a new budget for a specific user
    @PostMapping("/create/{userId}")
    public ResponseEntity<Object> createBudget(@PathVariable Long userId, @RequestBody Budget budget) {
        try {
            Budget createdBudget = budgetService.createBudgetForUser(userId, budget);
            return new ResponseEntity<>(createdBudget, HttpStatus.CREATED);
        } catch (Exception e) {
            ErrorResponse errorResponse = new ErrorResponse("Error creating budget for User ID: " + userId + ": " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Update an existing budget for a specific user
    @PutMapping("/update/{userId}/{id}")
    public ResponseEntity<Object> updateBudget(@PathVariable Long userId, @PathVariable Long id, @RequestBody Budget budgetDetails) {
        try {
            Budget updatedBudget = budgetService.updateBudgetForUser(userId, id, budgetDetails);
            return new ResponseEntity<>(updatedBudget, HttpStatus.OK);
        } catch (NoSuchElementException e) {
            ErrorResponse errorResponse = new ErrorResponse("Budget not found with ID: " + id + " for User ID: " + userId, HttpStatus.NOT_FOUND.value());
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            ErrorResponse errorResponse = new ErrorResponse("Error updating budget for User ID: " + userId + ": " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Delete a budget by ID and User
    @DeleteMapping("/delete/{userId}/{id}")
    public ResponseEntity<Object> deleteBudget(@PathVariable Long userId, @PathVariable Long id) {
        try {
            budgetService.deleteBudgetForUser(userId, id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (NoSuchElementException e) {
            ErrorResponse errorResponse = new ErrorResponse("Budget not found with ID: " + id + " for User ID: " + userId, HttpStatus.NOT_FOUND.value());
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            ErrorResponse errorResponse = new ErrorResponse("Error deleting budget for User ID: " + userId + ": " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    // Get budget chart data for a specific user
    @GetMapping("/budgetChartData/{userId}")
    public ResponseEntity<Object> getBudgetChartData(@PathVariable Long userId) {
        try {
            List<Budget> budgets = budgetService.listAllBudgets(userId);

            List<String> categories = new ArrayList<>();
            List<BigDecimal> plannedAmounts = new ArrayList<>();
            List<BigDecimal> actualAmounts = new ArrayList<>();

            for (Budget budget : budgets) {
                categories.add(budget.getCategory());
                plannedAmounts.add(budget.getPlannedAmount());
                BigDecimal actualBudget = transactionService.sumTransactionsForMonth(userId, budget.getCategory());
                actualAmounts.add(actualBudget);
            }

            Map<String, List<?>> chartData = new HashMap<>();
            chartData.put("category", categories);
            chartData.put("plannedAmount", plannedAmounts);
            chartData.put("actualAmount", actualAmounts);

            return new ResponseEntity<>(chartData, HttpStatus.OK);
        } catch (Exception e) {
            ErrorResponse errorResponse = new ErrorResponse("Error generating budget chart data for User ID: " + userId + ": " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    // Delete multiple budgets for a specific user
    @DeleteMapping("/deleteMultipleBudgets/{userId}")
    public ResponseEntity<Object> deleteMultipleBudgets(@PathVariable Long userId, @RequestBody List<Long> budgetIds) {
        try {
            budgetService.deleteAllBudgetsForUser(userId, budgetIds);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (NoSuchElementException e) {
            ErrorResponse errorResponse = new ErrorResponse("One or more budgets not found for user ID: " + userId, HttpStatus.NOT_FOUND.value());
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            ErrorResponse errorResponse = new ErrorResponse("Error deleting multiple budgets for User ID: " + userId + ": " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}



