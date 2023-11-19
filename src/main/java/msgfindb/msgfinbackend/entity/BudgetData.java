package msgfindb.msgfinbackend.entity;

import java.math.BigDecimal;

public class BudgetData {
    private Long id;
    private Long userId;
    private String category;
    private BigDecimal plannedAmount;
    private BigDecimal actualAmount;
}
