package com.radiuk.belavia_task.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Builder
public class RandomRecord {
    private LocalDate date;
    private String latin;
    private String cyrillic;
    private int evenInt;
    private BigDecimal decimal;
}
