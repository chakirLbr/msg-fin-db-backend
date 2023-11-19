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

    public List<Transaction> getAllTransactionsByUserId(Long userId) {
        // Assuming the repository has a method findByUserId
        return transactionRepository.findByUserId(userId);
    }

    public Page<Transaction> getTransactionsPaginated(Long userId, Pageable pageable, Map<String, String> filters) {
        Specification<Transaction> spec = createSpecificationWithUserId(userId, filters);
        return transactionRepository.findAll(spec, pageable);
    }

    private Specification<Transaction> createSpecificationWithUserId(Long userId, Map<String, String> filters) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Add a predicate to filter by userId
            predicates.add(criteriaBuilder.equal(root.get("userId"), userId));

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

    public Transaction createTransaction(Long userId, Transaction transaction) {
        // Set the userId for the transaction before saving
        transaction.setUserId(userId);
        return transactionRepository.save(transaction);
    }

    public Transaction getTransactionById(Long userId, Long transactionId) {
        // Assuming the repository has a method findByIdAndUserId
        return transactionRepository.findByIdAndUserId(transactionId, userId)
                .orElseThrow(() -> new NoSuchElementException("Transaction not found with id: " + transactionId));
    }

    public Transaction updateTransaction(Long userId, Long transactionId, Transaction updatedTransaction) {
        Transaction transaction = transactionRepository.findByIdAndUserId(transactionId, userId)
                .orElseThrow(() -> new NoSuchElementException("Transaction not found with id: " + transactionId));

        // Update the transaction details
        transaction.setName(updatedTransaction.getName());
        transaction.setDescription(updatedTransaction.getDescription());
        transaction.setAmount(updatedTransaction.getAmount());
        transaction.setDate(updatedTransaction.getDate());
        transaction.setCategory(updatedTransaction.getCategory());

        return transactionRepository.save(transaction);
    }

    public void deleteTransaction(Long userId, Long transactionId) {
        if (transactionRepository.existsByIdAndUserId(transactionId, userId)) {
            transactionRepository.deleteById(transactionId);
        } else {
            throw new NoSuchElementException("Transaction not found with id: " + transactionId);
        }
    }

    public void deleteAllTransactionsWithID(Long userId, List<Long> transactionIds) {
        // Modify to delete only transactions belonging to the user
        transactionRepository.deleteByIdInAndUserId(transactionIds, userId);
    }

    public Map<String, BigDecimal> sumPositiveAndNegativeTransactions(Long userId) {
        List<Transaction> transactions = getAllTransactionsByUserId(userId);
        BigDecimal sumPositive = BigDecimal.ZERO;
        BigDecimal sumNegative = BigDecimal.ZERO;

        for (Transaction transaction : transactions) {
            if (transaction.getAmount().compareTo(BigDecimal.ZERO) > 0) {
                sumPositive = sumPositive.add(transaction.getAmount());
            } else {
                sumNegative = sumNegative.add(transaction.getAmount());
            }
        }

        Map<String, BigDecimal> sums = new HashMap<>();
        sums.put("positive", sumPositive);
        sums.put("negative", sumNegative);
        return sums;
    }

    public List<Transaction> getTransactionsForWeek(Long userId, LocalDate startOfWeek, LocalDate endOfWeek) {
        // Convert LocalDate to LocalDateTime at the start and end of each day
        LocalDateTime startDateTime = startOfWeek.atStartOfDay();
        LocalDateTime endDateTime = endOfWeek.atTime(23, 59, 59);

        // Assuming the repository has a method findByUserIdAndDateBetween
        return transactionRepository.findByUserIdAndDateBetween(userId, startDateTime, endDateTime);
    }

    public BigDecimal sumTransactionsForMonth(Long userId, String category) {
        LocalDate startOfMonth = LocalDate.now().withDayOfMonth(1);
        LocalDate endOfMonth = startOfMonth.plusMonths(1).minusDays(1);
        List<Transaction> transactions = transactionRepository.findByUserIdAndDateBetween(userId, startOfMonth.atStartOfDay(), endOfMonth.atTime(23, 59, 59));

        return transactions.stream()
                .filter(t -> t.getCategory().equals(category))
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }


}
