package msgfindb.msgfinbackend.controller;


import msgfindb.msgfinbackend.entity.Budget;
import msgfindb.msgfinbackend.service.BudgetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;

@RestController
    @RequestMapping("/api/budget")
    public class BudgetController {

        @Autowired
        private BudgetService budgetService;

        @GetMapping("/api/listAllBudgets")
        public ResponseEntity<List<Budget>> listAllBudgets(@RequestParam Long UserId) {
            List<Budget> budgets= budgetService.listAllBudgets(UserId);
            return new ResponseEntity<>(budgets, HttpStatus.OK);
        }
        @GetMapping
        public ResponseEntity<List<Budget>> getAllBudgets() {
            List<Budget> budgets = budgetService.getAllBudgets();
            return new ResponseEntity<>(budgets, HttpStatus.OK);
        }

        @GetMapping("/budget/{id}")
        public ResponseEntity<Budget> getBudgetById(@PathVariable Long id) {
            Budget budget = budgetService.getBudgetById(id);
            return new ResponseEntity<>(budget, HttpStatus.OK);
        }

        @PostMapping("/createBudget")
        public ResponseEntity<Budget> createBudget(@RequestBody Budget budget) {
            Budget createdBudget = budgetService.createBudget(budget);
            return new ResponseEntity<>(createdBudget, HttpStatus.CREATED);
        }

        @PutMapping("/updateBudget/{id}")
        public ResponseEntity<Budget> updateBudget(@PathVariable Long id, @RequestBody Budget updatedBudget) {
            Budget updated = budgetService.updateBudget(id, updatedBudget);
            return new ResponseEntity<>(updated, HttpStatus.OK);
        }
        @PatchMapping("/updatePlannedAmount/{id}")
        public ResponseEntity<String> updatePlannedAmount(@PathVariable Long id, @RequestParam BigDecimal newPlannedAmount) {
            int rowsAffected = budgetService.updatePlannedAmount(id, newPlannedAmount);

            if (rowsAffected > 0) {
                return ResponseEntity.ok("Planned amount updated successfully.");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Budget not found with ID: " + id);
            }
        }
        @DeleteMapping("/deleteBudget/{id}")
        public ResponseEntity<String> deleteBudget(@PathVariable Long id) {
            try {
                budgetService.deleteBudget(id);
                return new ResponseEntity<>("Budget successfully deleted", HttpStatus.NO_CONTENT);
            } catch (NoSuchElementException e) {
                return new ResponseEntity<>("Budget not found with id: " + id, HttpStatus.NOT_FOUND);
            }
        }

    }



