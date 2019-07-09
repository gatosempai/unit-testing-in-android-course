package com.techyourchance.unittestingfundamentals.exercise2;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class StringDuplicatorTest {

    StringDuplicator stringDuplicator;

    @Before
    public void setUp() throws Exception {
        stringDuplicator = new StringDuplicator();
    }

    @Test
    public void duplicator_emptyString_returned() {
        String result = stringDuplicator.duplicate("");
        Assert.assertThat(result, CoreMatchers.is(""));
    }

    @Test
    public void duplicator_singleString_returned() {
        String result = stringDuplicator.duplicate("a");
        Assert.assertThat(result, CoreMatchers.is("aa"));
    }

    @Test
    public void duplicator_longString_returned() {
        String result = stringDuplicator.duplicate("hello");
        Assert.assertThat(result, CoreMatchers.is("hellohello"));
    }
}