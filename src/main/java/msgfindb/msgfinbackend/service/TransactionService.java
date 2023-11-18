package msgfindb.msgfinbackend.service;

import msgfindb.msgfinbackend.entity.Transaction;
import msgfindb.msgfinbackend.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;
    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    public Transaction createTransaction(Transaction transaction) {
        return transactionRepository.save(transaction);
    }
    public Transaction getTransactionById(Long id) {
        return transactionRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Transaction not found with id: " + id));
    }

    public Transaction updateTransaction(Long id, Transaction updatedTransaction) {
        Optional<Transaction> optionalTransaction = transactionRepository.findById(id);

        if (optionalTransaction.isPresent()) {
            Transaction existingTransaction = optionalTransaction.get();
            existingTransaction.setName(updatedTransaction.getName());
            existingTransaction.setDescription(updatedTransaction.getDescription());
            existingTransaction.setAmount(updatedTransaction.getAmount());
            existingTransaction.setDate(updatedTransaction.getDate());
            existingTransaction.setCategory(updatedTransaction.getCategory());
            existingTransaction.setAccountId(updatedTransaction.getAccountId());

            return transactionRepository.save(existingTransaction);
        } else {
            throw new NoSuchElementException("Transaction not found with id: " + id);
        }
    }

    public void deleteTransaction(Long id) {
        if (transactionRepository.existsById(id)) {
            transactionRepository.deleteById(id);
        } else {
            throw new NoSuchElementException("Transaction not found with id: " + id);
        }
    }
    public Transaction saveTransaction(Transaction transaction){
        return transactionRepository.save(transaction);
    }





    // Implement methods for creating, updating, and retrieving transactions
}
