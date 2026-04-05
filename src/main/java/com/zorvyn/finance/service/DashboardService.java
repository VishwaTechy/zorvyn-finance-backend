package com.zorvyn.finance.service;

import com.zorvyn.finance.repository.FinancialRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

import java.time.LocalDate;

import com.zorvyn.finance.model.FinancialRecord;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final FinancialRecordRepository repository;

    public Map<String, Object> getSummary() {
        BigDecimal income = repository.sumByType("INCOME");
        BigDecimal expense = repository.sumByType("EXPENSE");
        BigDecimal net = income.subtract(expense);

        Map<String, Object> summary = new LinkedHashMap<>();
        summary.put("totalIncome", income);
        summary.put("totalExpenses", expense);
        summary.put("netBalance", net);
        summary.put("recentActivity", repository.findRecentActivity());

        List<Object[]> rawCategory = repository.sumByCategory();
        Map<String, BigDecimal> categoryTotals = new LinkedHashMap<>();
        for (Object[] row : rawCategory) {
            categoryTotals.put((String) row[0], (BigDecimal) row[1]);
        }
        summary.put("categoryWiseTotals", categoryTotals);
        return summary;
    }

    public List<Map<String, Object>> getTrend() {
    List<FinancialRecord> records = repository.findByDeletedFalse();
    Map<LocalDate, BigDecimal> dailyNet = new java.util.TreeMap<>();
    for (var r : records) {
        BigDecimal amt = r.getType().equals("INCOME") ? r.getAmount() : r.getAmount().negate();
        dailyNet.merge(r.getDate(), amt, BigDecimal::add);
    }
    List<Map<String, Object>> trend = new java.util.ArrayList<>();
    BigDecimal running = BigDecimal.ZERO;
    for (var entry : dailyNet.entrySet()) {
        running = running.add(entry.getValue());
        Map<String, Object> point = new LinkedHashMap<>();
        point.put("date", entry.getKey().toString());
        point.put("balance", running);
        trend.add(point);
    }
    return trend;
 }
 public Map<String, Object> getInsights() {
    List<FinancialRecord> records = repository.findByDeletedFalse();
    Map<String, Object> insights = new LinkedHashMap<>();

    // Net balance
    BigDecimal income = repository.sumByType("INCOME");
    BigDecimal expense = repository.sumByType("EXPENSE");
    insights.put("netBalance", income.subtract(expense));
    insights.put("totalIncome", income);
    insights.put("totalExpenses", expense);

    // Top expense category
    records.stream()
        .filter(r -> r.getType().equals("EXPENSE"))
        .collect(java.util.stream.Collectors.groupingBy(
            FinancialRecord::getCategory,
            java.util.stream.Collectors.reducing(BigDecimal.ZERO, FinancialRecord::getAmount, BigDecimal::add)
        ))
        .entrySet().stream()
        .max(java.util.Map.Entry.comparingByValue())
        .ifPresent(e -> {
            insights.put("topExpenseCategory", e.getKey());
            insights.put("topExpenseAmount", e.getValue());
        });

    // Largest transaction
    records.stream()
        .max(java.util.Comparator.comparing(FinancialRecord::getAmount))
        .ifPresent(r -> {
            insights.put("largestTransactionType", r.getType());
            insights.put("largestTransactionAmount", r.getAmount());
            insights.put("largestTransactionCategory", r.getCategory());
        });

    // Category wise for donut (expenses only)
    Map<String, BigDecimal> expenseByCategory = new LinkedHashMap<>();
    records.stream()
        .filter(r -> r.getType().equals("EXPENSE"))
        .collect(java.util.stream.Collectors.groupingBy(
            FinancialRecord::getCategory,
            java.util.stream.Collectors.reducing(BigDecimal.ZERO, FinancialRecord::getAmount, BigDecimal::add)
        ))
        .entrySet().stream()
        .sorted(java.util.Map.Entry.<String, BigDecimal>comparingByValue().reversed())
        .forEach(e -> expenseByCategory.put(e.getKey(), e.getValue()));
    insights.put("expenseByCategory", expenseByCategory);

    return insights;
}
}