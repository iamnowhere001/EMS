package com.ems.common;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class TaxCalculatorTest {

    @Test
    void testNoTaxWhenBelowThreshold() {
        BigDecimal tax = TaxCalculator.calculateMonthlyTax(
                new BigDecimal("4000"),
                BigDecimal.ZERO,
                BigDecimal.ZERO
        );
        assertEquals(BigDecimal.ZERO, tax);
    }

    @Test
    void testTaxBracket1() {
        BigDecimal tax = TaxCalculator.calculateMonthlyTax(
                new BigDecimal("6000"),
                BigDecimal.ZERO,
                BigDecimal.ZERO
        );
        assertTrue(tax.compareTo(new BigDecimal("30")) <= 0);
    }

    @Test
    void testTaxBracket2() {
        BigDecimal tax = TaxCalculator.calculateMonthlyTax(
                new BigDecimal("15000"),
                BigDecimal.ZERO,
                BigDecimal.ZERO
        );
        assertEquals(new BigDecimal("790.00"), tax);
    }

    @Test
    void testTaxWithSocialSecurity() {
        BigDecimal tax = TaxCalculator.calculateMonthlyTax(
                new BigDecimal("10000"),
                new BigDecimal("1000"),
                new BigDecimal("1200")
        );
        assertTrue(tax.compareTo(BigDecimal.ZERO) > 0);
    }

    @Test
    void testHashUtilSha256() {
        String input = "test123";
        String hash = HashUtil.sha256(input);
        assertNotNull(hash);
        assertEquals(64, hash.length());
        assertEquals(HashUtil.sha256(input), hash);
    }

    @Test
    void testHashUtilGenerateSalt() {
        String salt1 = HashUtil.generateSalt();
        String salt2 = HashUtil.generateSalt();
        assertNotNull(salt1);
        assertNotNull(salt2);
        assertNotEquals(salt1, salt2);
        assertEquals(32, salt1.length());
    }
}