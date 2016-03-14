package com.gurkensalat.osm.mosques;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Countries
{
    private final static Map<String, String> countries = new HashMap<>();

    static
    {
        countries.put("albania", "AL");
        countries.put("andorra", "AD");
        countries.put("austria", "AT");
        countries.put("azores", "PT"); // Autonomous Region of the Azores
        countries.put("belarus", "BY");
        countries.put("belgium", "BE");
        countries.put("bosnia-herzegovina", "BA");
        countries.put("bulgaria", "BG");
        countries.put("croatia", "HR");
        countries.put("cyprus", "CY");
        countries.put("czech-republic", "CZ");
        countries.put("denmark", "DK");
        countries.put("estonia", "EE");
        countries.put("faroe-islands", "FO");
        countries.put("finland", "FI");
        countries.put("france", "FR");
        countries.put("georgia", "GE");
        countries.put("germany", "DE");
        countries.put("great-britain", "GB"); // United Kingdom
        countries.put("greece", "GR");
        countries.put("hungary", "HU");
        countries.put("iceland", "IS");
        countries.put("ireland-and-northern-ireland", "IE");
        countries.put("isle-of-man", "IM");
        countries.put("italy", "IT");
        countries.put("kosovo", "XK");
        countries.put("latvia", "LV");
        countries.put("liechtenstein", "LI");
        countries.put("lithuania", "LT");
        countries.put("luxembourg", "LU");
        countries.put("macedonia", "MK");
        countries.put("malta", "MT");
        countries.put("moldova", "MD");
        countries.put("monaco", "MC");
        countries.put("montenegro", "ME");
        countries.put("netherlands", "NL");
        countries.put("norway", "NO");
        countries.put("poland", "PL");
        countries.put("portugal", "PT");
        countries.put("romania", "RO");
        countries.put("serbia", "RS");
        countries.put("slovakia", "SK");
        countries.put("slovenia", "SI");
        countries.put("spain", "ES");
        countries.put("sweden", "SE");
        countries.put("switzerland", "CH");
        countries.put("turkey", "TR");
        countries.put("ukraine", "UA");
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
