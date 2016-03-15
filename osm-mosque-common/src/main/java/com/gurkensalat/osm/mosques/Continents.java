package com.gurkensalat.osm.mosques;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Continents
{
    private final static Map<String, String> continents = new HashMap<>();

    static
    {
        continents.put("africa", "Z1");
        continents.put("antarctica", "Z2");
        continents.put("asia", "Z3");
        continents.put("australia-oceania", "Z4");
        continents.put("central-america", "Z5");
        continents.put("europe", "Z6");
        continents.put("north-america", "Z7");
        continents.put("south-america", "Z8");
    }

    public static Map<String, String> getContinents()
    {
        return continents;
    }
}
