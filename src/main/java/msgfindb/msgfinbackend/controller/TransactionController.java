package msgfindb.msgfinbackend.controller;

import msgfindb.msgfinbackend.ErrorResponse;
import msgfindb.msgfinbackend.entity.Transaction;
import msgfindb.msgfinbackend.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @GetMapping("/sumTransactions/{userId}")
    public ResponseEntity<Object> sumPositiveAndNegativeTransactions(@PathVariable Long userId) {
        try {
            Map<String, BigDecimal> sums = transactionService.sumPositiveAndNegativeTransactions(userId);
            return new ResponseEntity<>(sums, HttpStatus.OK);
        } catch (Exception e) {
            ErrorResponse errorResponse = new ErrorResponse("Error calculating sums of transactions: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/getTransactionsPaginated/{userId}")
    public ResponseEntity<Object> getAllTransactions(
            @PathVariable Long userId,
            @PageableDefault(sort = "date", direction = Sort.Direction.DESC) Pageable pageable,
            @RequestParam Map<String, String> filters) {
        try {
            Page<Transaction> transactions = transactionService.getTransactionsPaginated(userId, pageable, filters);
            return new ResponseEntity<>(transactions, HttpStatus.OK);
        } catch (Exception e) {
            ErrorResponse errorResponse = new ErrorResponse("Error retrieving paginated transactions: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/getAllTransactions/{userId}")
    public ResponseEntity<Object> getAllTransactions(@PathVariable Long userId) {
        try {
            List<Transaction> transactions = transactionService.getAllTransactionsByUserId(userId);
            return new ResponseEntity<>(transactions, HttpStatus.OK);
        } catch (Exception e) {
            ErrorResponse errorResponse = new ErrorResponse("Error retrieving all transactions for user ID: " + userId + ": " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/getTransaction/{userId}/{transactionId}")
    public ResponseEntity<Object> getTransactionById(@PathVariable Long userId, @PathVariable Long transactionId) {
        try {
            Transaction transaction = transactionService.getTransactionById(userId, transactionId);
            return new ResponseEntity<>(transaction, HttpStatus.OK);
        } catch (NoSuchElementException e) {
            ErrorResponse errorResponse = new ErrorResponse("Transaction not found for user ID: " + userId + " with transaction ID: " + transactionId, HttpStatus.NOT_FOUND.value());
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/createTransaction/{userId}")
    public ResponseEntity<Object> createTransaction(@PathVariable Long userId, @RequestBody Transaction transaction) {
        try {
            Transaction createdTransaction = transactionService.createTransaction(userId, transaction);
            return new ResponseEntity<>(createdTransaction, HttpStatus.CREATED);
        } catch (Exception e) {
            ErrorResponse errorResponse = new ErrorResponse("Error creating transaction: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/updateTransaction/{userId}/{transactionId}")
    public ResponseEntity<Object> updateTransaction(@PathVariable Long userId, @PathVariable Long transactionId, @RequestBody Transaction updatedTransaction) {
        try {
            Transaction updated = transactionService.updateTransaction(userId, transactionId, updatedTransaction);
            return new ResponseEntity<>(updated, HttpStatus.OK);
        } catch (NoSuchElementException e) {
            ErrorResponse errorResponse = new ErrorResponse("Transaction not found for user ID: " + userId + " with transaction ID: " + transactionId, HttpStatus.NOT_FOUND.value());
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            ErrorResponse errorResponse = new ErrorResponse("Error updating transaction: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/candlestickChartData/{userId}")
    public ResponseEntity<Object> getCandlestickChartData(@PathVariable Long userId) {
        try {
            // Fetch all transactions for the given user
            List<Transaction> transactions = transactionService.getAllTransactionsByUserId(userId);

            // Transform transactions into a format suitable for the candlestick chart
            Map<LocalDate, List<BigDecimal>> groupedTransactions = transactions.stream()
                    .collect(Collectors.groupingBy(
                            transaction -> transaction.getDate().toLocalDate(),
                            Collectors.mapping(Transaction::getAmount, Collectors.toList())
                    ));

            List<Map<String, Object>> chartData = groupedTransactions.entrySet().stream().map(entry -> {
                List<BigDecimal> amounts = entry.getValue();
                BigDecimal open = amounts.get(0);
                BigDecimal close = amounts.get(amounts.size() - 1);
                BigDecimal high = Collections.max(amounts);
                BigDecimal low = Collections.min(amounts);

                Map<String, Object> dataPoint = new HashMap<>();
                dataPoint.put("date", entry.getKey().toString()); // Converts LocalDate to String
                dataPoint.put("open", open);
                dataPoint.put("close", close);
                dataPoint.put("high", high);
                dataPoint.put("low", low);

                return dataPoint;
            }).collect(Collectors.toList());

            return new ResponseEntity<>(chartData, HttpStatus.OK);
        } catch (Exception e) {
            ErrorResponse errorResponse = new ErrorResponse("Error generating chart data: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/weekScatterChart/{userId}")
    public ResponseEntity<Object> getWeekScatterChartData(@PathVariable Long userId) {
        try {
            LocalDate today = LocalDate.now();
            LocalDate monday = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
            LocalDate sunday = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));

            // Fetch transactions for the current week
            List<Transaction> weeklyTransactions = transactionService.getTransactionsForWeek(userId, monday, sunday);

            // Process data for the scatter chart
            Map<String, Object> scatterChartData = processForScatterChart(weeklyTransactions);

            return new ResponseEntity<>(scatterChartData, HttpStatus.OK);
        } catch (Exception e) {
            ErrorResponse errorResponse = new ErrorResponse("Error retrieving scatter chart data: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    private Map<String, Object> processForScatterChart(List<Transaction> transactions) {
        int[][] aggregatedAmounts = new int[7][24]; // 7 days, 24 hours
        int minNegativeValue = 0;
        int maxNegativeValue = Integer.MIN_VALUE;

        // Initialize with zeros and aggregate the amounts for negative transactions
        for (Transaction transaction : transactions) {
            int amount = transaction.getAmount().intValue(); // Assuming amount is an integer
            if (amount < 0) {
                LocalDate date = transaction.getDate().toLocalDate();
                int dayOfWeekIndex = date.getDayOfWeek().getValue() % 7; // Monday as 0
                int hourOfDayIndex = transaction.getDate().getHour();

                aggregatedAmounts[dayOfWeekIndex][hourOfDayIndex] += amount;
                minNegativeValue = Math.min(minNegativeValue, amount);
                maxNegativeValue = Math.max(maxNegativeValue, amount);
            }
        }

        // Prepare scatter data
        List<List<Object>> scatterData = new ArrayList<>();
        for (int i = 0; i < aggregatedAmounts.length; i++) {
            for (int j = 0; j < aggregatedAmounts[i].length; j++) {
                int value = aggregatedAmounts[i][j];
                if (value < 0) {
                    scatterData.add(Arrays.asList(i, j, value));
                }
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("scatterData", scatterData);
        result.put("minValue", minNegativeValue);
        result.put("maxValue", maxNegativeValue);
        return result;
    }


    @DeleteMapping("/deleteTransaction/{userId}/{transactionId}")
    public ResponseEntity<Object> deleteTransaction(@PathVariable Long userId, @PathVariable Long transactionId) {
        try {
            transactionService.deleteTransaction(userId, transactionId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (NoSuchElementException e) {
            ErrorResponse errorResponse = new ErrorResponse("Transaction not found for user ID: " + userId + " with transaction ID: " + transactionId, HttpStatus.NOT_FOUND.value());
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            ErrorResponse errorResponse = new ErrorResponse("Error deleting transaction: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/deleteMultipleTransactions/{userId}")
    public ResponseEntity<Object> deleteMultipleTransactions(@PathVariable Long userId, @RequestBody List<Long> transactionIds) {
        try {
            transactionService.deleteAllTransactionsWithID(userId, transactionIds);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (NoSuchElementException e) {
            ErrorResponse errorResponse = new ErrorResponse("One or more transactions not found for user ID: " + userId, HttpStatus.NOT_FOUND.value());
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            ErrorResponse errorResponse = new ErrorResponse("Error deleting multiple transactions: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
