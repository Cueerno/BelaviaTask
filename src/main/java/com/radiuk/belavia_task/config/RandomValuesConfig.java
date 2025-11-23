package com.radiuk.belavia_task.config;

import lombok.*;

@Getter
public class RandomValuesConfig {

    private final PropertyConfig propertyConfig;

    private final int dateYearsBack;
    private final int latinLength;
    private final int russianLength;
    private final int evenIntMin;
    private final int evenIntMax;
    private final int decimalMin;
    private final int decimalMax;
    private final int decimalScale;

    public RandomValuesConfig(PropertyConfig propertyConfig) {
        this.propertyConfig = propertyConfig;
        this.dateYearsBack = propertyConfig.getInt("random.date-years-back");
        this.latinLength = propertyConfig.getInt("random.latin-length");
        this.russianLength = propertyConfig.getInt("random.russian-length");
        this.evenIntMin = propertyConfig.getInt("random.even-int-min");
        this.evenIntMax = propertyConfig.getInt("random.even-int-max");
        this.decimalMin = propertyConfig.getInt("random.decimal-min");
        this.decimalMax = propertyConfig.getInt("random.decimal-max");
        this.decimalScale = propertyConfig.getInt("random.decimal-scale");
    }
}
