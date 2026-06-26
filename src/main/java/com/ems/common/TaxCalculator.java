package com.ems.common;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class TaxCalculator {

    private static final BigDecimal TAX_THRESHOLD = new BigDecimal("5000");

    private static final BigDecimal[][] TAX_BRACKETS = {
            {new BigDecimal("0"), new BigDecimal("36000"), new BigDecimal("0.03"), new BigDecimal("0")},
            {new BigDecimal("36000"), new BigDecimal("144000"), new BigDecimal("0.10"), new BigDecimal("2520")},
            {new BigDecimal("144000"), new BigDecimal("300000"), new BigDecimal("0.20"), new BigDecimal("16920")},
            {new BigDecimal("300000"), new BigDecimal("420000"), new BigDecimal("0.25"), new BigDecimal("31920")},
            {new BigDecimal("420000"), new BigDecimal("660000"), new BigDecimal("0.30"), new BigDecimal("52920")},
            {new BigDecimal("660000"), new BigDecimal("960000"), new BigDecimal("0.35"), new BigDecimal("85920")},
            {new BigDecimal("960000"), new BigDecimal("999999999"), new BigDecimal("0.45"), new BigDecimal("181920")}
    };

    public static BigDecimal calculateMonthlyTax(BigDecimal grossSalary, BigDecimal socialSecurity, BigDecimal housingFund) {
        BigDecimal taxableIncome = grossSalary
                .subtract(socialSecurity != null ? socialSecurity : BigDecimal.ZERO)
                .subtract(housingFund != null ? housingFund : BigDecimal.ZERO)
                .subtract(TAX_THRESHOLD);

        if (taxableIncome.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal annualTaxableIncome = taxableIncome.multiply(new BigDecimal("12"));
        BigDecimal annualTax = calculateAnnualTax(annualTaxableIncome);
        return annualTax.divide(new BigDecimal("12"), 2, RoundingMode.HALF_UP);
    }

    private static BigDecimal calculateAnnualTax(BigDecimal annualTaxableIncome) {
        for (BigDecimal[] bracket : TAX_BRACKETS) {
            BigDecimal lower = bracket[0];
            BigDecimal upper = bracket[1];
            BigDecimal rate = bracket[2];
            BigDecimal deduction = bracket[3];

            if (annualTaxableIncome.compareTo(lower) > 0 && annualTaxableIncome.compareTo(upper) <= 0) {
                return annualTaxableIncome.multiply(rate)
                        .subtract(deduction)
                        .setScale(2, RoundingMode.HALF_UP);
            }
        }
        return BigDecimal.ZERO;
    }

    public static BigDecimal calculateAnnualTaxDirect(BigDecimal annualTaxableIncome) {
        if (annualTaxableIncome.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }
        return calculateAnnualTax(annualTaxableIncome);
    }
}