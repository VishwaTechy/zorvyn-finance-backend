package com.zorvyn.finance.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class RecordRequest {
    @NotNull @Positive private BigDecimal amount;
    @NotBlank @Pattern(regexp = "INCOME|EXPENSE") private String type;
    @NotBlank private String category;
    @NotNull private LocalDate date;
    private String notes;
}