package com.techyourchance.unittestingfundamentals.exercise1;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;

public class NegativeNumberValidatorTest {
    NegativeNumberValidator negativeNumberValidator;

    @Before
    public void setUp() {
        negativeNumberValidator = new NegativeNumberValidator();
    }

    @Test
    public void minusNumber() {
        boolean result = negativeNumberValidator.isNegative(-1);
        Assert.assertThat(result, is(true));
    }

    @Test
    public void zeroNumber() {
        boolean result = negativeNumberValidator.isNegative(0);
        Assert.assertThat(result, is(false));
    }

    @Test
    public void plusNumber() {
        boolean result = negativeNumberValidator.isNegative(1);
        Assert.assertThat(result, is(false));
    }

}