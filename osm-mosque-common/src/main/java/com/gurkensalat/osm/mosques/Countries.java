package com.gurkensalat.osm.mosques;

import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class Countries
{
    private final static Map<String, String> countries = new HashMap<>();

    static
    {
        ResourceBundle bundle = ResourceBundle.getBundle("i18n/countryNames");
        if (bundle != null)
        {
            Enumeration<String> keys = bundle.getKeys();
            while (keys.hasMoreElements())
            {
                String key = keys.nextElement();
                String value = bundle.getString(key);

                countries.put(key, value);
            }
        }
    }

    private final static List<String> germanyStates =
            Arrays.asList(
                    "baden-wuerttemberg",
                    "bayern",
                    "berlin",
                    "brandenburg",
                    "bremen",
                    "hamburg",
                    "hessen",
                    "mecklenburg-vorpommern",
                    "niedersachsen",
                    "nordrhein-westfalen",
                    "rheinland-pfalz",
                    "saarland",
                    "sachsen-anhalt",
                    "sachsen",
                    "schleswig-holstein",
                    "thueringen"
            );

    public static Map<String, String> getCountries()
    {
        return countries;
    }

    public static List<String> getGermanyStates()
    {
        return germanyStates;
    }
}
