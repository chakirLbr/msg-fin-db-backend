package msgfindb.msgfinbackend.service;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;
import msgfindb.msgfinbackend.entity.Transaction;
import msgfindb.msgfinbackend.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    // In TransactionService class
    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll(); // Assuming you have a JPA repository
    }


    public Page<Transaction> getTransactionsPaginated(Pageable pageable, Map<String, String> filters) {
        Specification<Transaction> spec = createSpecification(filters);
        return transactionRepository.findAll(spec, pageable);
    }

    private Specification<Transaction> createSpecification(Map<String, String> filters) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            filters.forEach((key, value) -> {
                if (key.startsWith("filter_")) {
                    String[] parts = key.split("_");
                    if (parts.length < 3) return;

                    String field = parts[1];
                    String matchMode = parts[2];
                    Expression<String> path = root.get(field);

                    switch (matchMode) {
                        case "startsWith":
                            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(path), value.toLowerCase() + "%"));
                            break;
                        case "contains":
                            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(path), "%" + value.toLowerCase() + "%"));
                            break;
                        case "equals":
                            predicates.add(criteriaBuilder.equal(path, value));
                            break;
                        // Add more cases for other text-based match modes

                        case "lt":
                        case "lte":
                        case "gt":
                        case "gte":
                            // Handle numeric comparisons
                            if (field.equals("amount")) {
                                try {
                                    BigDecimal amount = new BigDecimal(value);
                                    // Apply numeric comparison based on matchMode
                                    switch (matchMode) {
                                        case "lt":
                                            predicates.add(criteriaBuilder.lessThan(root.get(field), amount));
                                            break;
                                        case "lte":
                                            predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get(field), amount));
                                            break;
                                        case "gt":
                                            predicates.add(criteriaBuilder.greaterThan(root.get(field), amount));
                                            break;
                                        case "gte":
                                            predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get(field), amount));
                                            break;
                                    }
                                } catch (NumberFormatException e) {
                                    // Handle exception
                                }
                            }
                            break;
                        case "notContains":
                            predicates.add(criteriaBuilder.notLike(criteriaBuilder.lower(path), "%" + value.toLowerCase() + "%"));
                            break;
                        case "endsWith":
                            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(path), "%" + value.toLowerCase()));
                            break;
                        case "notEquals":
                            predicates.add(criteriaBuilder.notEqual(path, value));
                            break;
                        case "in":
                            // Assuming 'value' is a comma-separated string of values
                            CriteriaBuilder.In<String> inClause = criteriaBuilder.in(path);
                            Arrays.stream(value.split(",")).forEach(inClause::value);
                            predicates.add(inClause);
                            break;
                        case "between":
                            // This assumes a numeric range given in 'value' like "10-20"
                            String[] range = value.split("-");
                            BigDecimal start = new BigDecimal(range[0]);
                            BigDecimal end = new BigDecimal(range[1]);
                            predicates.add(criteriaBuilder.between(root.get(field), start, end));
                            break;
                        case "isNot":
                            predicates.add(criteriaBuilder.notEqual(path, value));
                            break;
                        case "dateIsNot":
                            LocalDateTime date = LocalDate.parse(value, DateTimeFormatter.ISO_LOCAL_DATE).atStartOfDay();
                            predicates.add(criteriaBuilder.notEqual(root.get(field), date));
                            break;
                        case "dateIs":
                        case "dateBefore":
                        case "dateAfter":
                            // Handle date comparisons
                            if (field.equals("date")) {
                                try {
                                    LocalDateTime date2 = LocalDate.parse(value, DateTimeFormatter.ISO_LOCAL_DATE).atStartOfDay();
                                    // Apply date comparison based on matchMode
                                    switch (matchMode) {
                                        case "dateIs":
                                            predicates.add(criteriaBuilder.equal(root.get(field), date2));
                                            break;
                                        case "dateBefore":
                                            predicates.add(criteriaBuilder.lessThan(root.get(field), date2));
                                            break;
                                        case "dateAfter":
                                            predicates.add(criteriaBuilder.greaterThan(root.get(field), date2));
                                            break;
                                    }
                                } catch (DateTimeParseException e) {
                                    // Handle exception
                                }
                            }
                            break;
                    }
                }
            });

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
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

    public void deleteAllTransactionsWithID(List<Long> ids) {
        transactionRepository.deleteAllById(ids);
    }

    public void deleteTransaction(Long id) {
        if (transactionRepository.existsById(id)) {
            transactionRepository.deleteById(id);
        } else {
            throw new NoSuchElementException("Transaction not found with id: " + id);
        }

    }

    public Transaction saveTransaction(Transaction transaction) {
        return transactionRepository.save(transaction);
    }


}
