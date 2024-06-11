package com.example.telegramBot.service;

import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

@Service
public class TaxService {
    private static final int MONTHS = 12;
    private static final Map<Integer, Double> TAX_STEPS_NEW = new LinkedHashMap<>();
    static {
        TAX_STEPS_NEW.put(50000000, 0.22);
        TAX_STEPS_NEW.put(20000000, 0.20);
        TAX_STEPS_NEW.put(5000000, 0.18);
        TAX_STEPS_NEW.put(2400000, 0.15);
        TAX_STEPS_NEW.put(0, 0.13);
    }

    public double countTax(double salary) {
        double yearSalary = salary * MONTHS;
        return countTax(yearSalary, TAX_STEPS_NEW);
    }
    private double countTax(double salary, Map<Integer, Double> TAX_STEPS) {
        if (salary <= 0) {
            throw new IllegalArgumentException("Зарплата должна быть больше нуля");
        }

        double tax = 0;

        for (Map.Entry<Integer, Double> step : TAX_STEPS.entrySet()) {
            if (salary > step.getKey()) {
                tax += (salary - step.getKey()) * step.getValue();
                salary = step.getKey();
            }
        }
        return Double.parseDouble(String.format(Locale.ENGLISH, "%.2f", tax / MONTHS));
    }
}
