package msgfindb.msgfinbackend;


import com.github.javafaker.Faker;
import msgfindb.msgfinbackend.entity.AccessRights;
import msgfindb.msgfinbackend.entity.Budget;
import msgfindb.msgfinbackend.entity.Transaction;
import msgfindb.msgfinbackend.entity.User;
import msgfindb.msgfinbackend.repository.BudgetRepository;
import msgfindb.msgfinbackend.repository.TransactionRepository;
import msgfindb.msgfinbackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class DataGenerator {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private BudgetRepository budgetRepository;

    private static final int NUM_USERS = 10;
    private static final int TOTAL_TRANSACTIONS_PER_USER = 200;
    private static final int RECENT_TRANSACTIONS_PER_USER = 50;
    private static final int NUM_BUDGET_CATEGORIES = 10;

    private static Faker faker = new Faker();

    public void generateAndSaveData() {
        List<User> users = this.generateUsers();
        userRepository.saveAll(users);

        List<Transaction> transactions = generateTransactions(users);
        transactionRepository.saveAll(transactions);

        List<Budget> budgets = generateBudgets(users);
        budgetRepository.saveAll(budgets);
    }

    private List<Budget> generateBudgets(List<User> users) {
        List<Budget> budgets = new ArrayList<>();

        for (User user : users) {
            for (int i = 0; i < NUM_BUDGET_CATEGORIES; i++) {
                budgets.add(createBudget(user));
            }
        }
        return budgets;
    }

    private Budget createBudget(User user) {
        Budget budget = new Budget();
        budget.setUserId(user.getId());
        budget.setCategory(faker.commerce().department());

        if (faker.bool().bool()) {
            budget.setPlannedAmount(new BigDecimal(faker.number().randomDouble(2, 100, 1000)));
        } else {
            budget.setPlannedAmount(null);
        }
        return budget;
    }


    public List<User> generateUsers() {
        List<User> users = new ArrayList<>();

        // Hardcoded admin user
        User admin = new User();
        admin.setRights(AccessRights.ADMIN);
        admin.setEmail("admin@admin.admin");
        admin.setUsername("admin");
        admin.setPassword(passwordEncoder.encode("password")); // Encode the password
        admin.setFirstname("Max");
        admin.setSurname("Musterfrau");

        // Set other details for admin...
        users.add(admin);

        // Generate dummy users
        for (int i = 0; i < NUM_USERS - 1; i++) {
            User user = new User();
            user.setFirstname(faker.name().firstName());
            user.setSurname(faker.name().lastName());
            user.setEmail(faker.internet().emailAddress());
            user.setUsername(faker.name().username());
            user.setPassword(passwordEncoder.encode(faker.internet().password()));
            users.add(user);
        }

        return users;
    }

    private LocalDateTime generateRandomDateTime(int daysAgoStart, int daysAgoEnd) {
        int randomDays = faker.number().numberBetween(daysAgoStart, daysAgoEnd);
        int randomHour = faker.number().numberBetween(0, 23);
        int randomMinute = faker.number().numberBetween(0, 59);
        int randomSecond = faker.number().numberBetween(0, 59);

        return LocalDateTime.now()
                .minusDays(randomDays)
                .withHour(randomHour)
                .withMinute(randomMinute)
                .withSecond(randomSecond);
    }

    public List<Transaction> generateTransactions(List<User> users) {
        List<Transaction> transactions = new ArrayList<>();

        for (User user : users) {
            // Generate recent transactions (past month)
            for (int i = 0; i < RECENT_TRANSACTIONS_PER_USER; i++) {
                transactions.add(createTransaction(user, generateRandomDateTime(1, 7)));
            }

            // Generate remaining transactions over the past year
            for (int i = 0; i < TOTAL_TRANSACTIONS_PER_USER - RECENT_TRANSACTIONS_PER_USER; i++) {
                transactions.add(createTransaction(user, generateRandomDateTime(31, 365)));
            }
        }

        return transactions;
    }

    private Transaction createTransaction(User user, LocalDateTime date) {
        Transaction transaction = new Transaction();
        transaction.setDescription(faker.commerce().productName());
        transaction.setName(faker.company().name());
        transaction.setAmount(new BigDecimal(faker.number().randomDouble(2, -1000, 1000)));
        transaction.setCategory(faker.commerce().department());
        transaction.setUserId(user.getId());
        transaction.setDate(date);
        return transaction;
    }
}
