package com.gurkensalat.osm.mosques;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class CountriesTest
{
    private Countries testable;

    @Before
    public void setUp()
    {
        testable = new Countries();
    }

    @Test
    public void testResourceLoading()
    {
        assertNotNull(testable);
        assertNotNull(testable.getCountries());
    }

    @Test
    public void testDefaultTurkey()
    {
        assertEquals("Turkey", testable.getCountries().get("tr"));
    }

    @Test
    public void testDefaultGreatBritain()
    {
        assertEquals("Great Britain", testable.getCountries().get("gb"));
    }

    @Test
    public void testDefaultUnknown()
    {
        assertEquals("Unknown", testable.getCountries().get("??"));
    }
}
