package msgfindb.msgfinbackend.controller;

import msgfindb.msgfinbackend.entity.Transaction;
import msgfindb.msgfinbackend.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @GetMapping("getAllTransactions")
    public ResponseEntity<List<Transaction>> getAllTransactions() {
        List<Transaction> transactions = transactionService.getAllTransactions();
        return new ResponseEntity<>(transactions, HttpStatus.OK);
    }
    @GetMapping("/getTransaction/{id}")
    public ResponseEntity<String> getTransactionById(@PathVariable Long id) {
        try {
            Transaction transaction = transactionService.getTransactionById(id);
            return new ResponseEntity<>("Transaction found:\n" + transaction.toString(), HttpStatus.OK);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>("Transaction not found with id: " + id, HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/createTransaction")
    public ResponseEntity<String> createTransaction(@RequestBody Transaction transaction) {
        try {
            Transaction createdTransaction = transactionService.createTransaction(transaction);
            return new ResponseEntity<>("Transaction successfully created:\n" + createdTransaction.toString(), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to create transaction. Error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/updateTransaction/{id}")
    public ResponseEntity<String> updateTransaction(@PathVariable Long id, @RequestBody Transaction updatedTransaction) {
        try {
            Transaction updated = transactionService.updateTransaction(id, updatedTransaction);
            return new ResponseEntity<>("Transaction successfully updated:\n" + updated.toString(), HttpStatus.OK);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>("Transaction not found with id: " + id, HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/deleteTransaction/{id}")
    public ResponseEntity<String> deleteTransaction(@PathVariable Long id) {
        try {
            transactionService.deleteTransaction(id);
            return new ResponseEntity<>("Transaction successfully deleted", HttpStatus.OK);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>("Transaction not found with id: " + id, HttpStatus.NOT_FOUND);
        }

    }

    @DeleteMapping("/deleteMultipleTransactions")
    public ResponseEntity<String> deleteTransaction(@RequestBody List<Long> ids) {
        try {
            transactionService.deleteAllTransactionsWithID(ids);
            return new ResponseEntity<>("Transactions successfully deleted", HttpStatus.NO_CONTENT);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>("One or more transactions not found", HttpStatus.NOT_FOUND);
        }
    }
}

