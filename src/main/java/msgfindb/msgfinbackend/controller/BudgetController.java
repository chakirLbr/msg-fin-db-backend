package msgfindb.msgfinbackend.controller;


import msgfindb.msgfinbackend.entity.Budget;
import msgfindb.msgfinbackend.service.BudgetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
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
    }



