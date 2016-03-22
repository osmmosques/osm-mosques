package com.gurkensalat.osm.mosques;

import com.gurkensalat.osm.mosques.entity.StatisticsEntry;

public class MapStatisticsDataEntry
{
    private String key;

    private double lat;

    private double lon;

    private String name;

    private String countryCode;

    private String countryName;

    private int osmMosqueNodes;

    private int osmMosqueWays;

    private int osmMosqueTotal;

    public MapStatisticsDataEntry(StatisticsEntry statisticsEntry)
    {
        this.setLat(statisticsEntry.getCentroidLat());
        this.setLon(statisticsEntry.getCentroidLon());
        this.setName(statisticsEntry.getCountryName());
        this.setCountryName(statisticsEntry.getCountryName());
        this.setCountryCode(statisticsEntry.getCountryName());

        if (statisticsEntry.getOsmMosqueNodes() != null)
        {
            this.setOsmMosqueNodes(statisticsEntry.getOsmMosqueNodes());
        }
        else
        {
            this.setOsmMosqueNodes(0);
        }

        if (statisticsEntry.getOsmMosqueWays() != null)
        {
            this.setOsmMosqueWays(statisticsEntry.getOsmMosqueWays());
        }
        else
        {
            this.setOsmMosqueWays(0);
        }

        this.setOsmMosqueTotal(this.getOsmMosqueNodes() + this.getOsmMosqueWays());
    }

    public String getKey()
    {
        return key;
    }

    public void setKey(String key)
    {
        this.key = key;
    }

    public double getLat()
    {
        return lat;
    }

    public void setLat(double lat)
    {
        this.lat = lat;
    }

    public double getLon()
    {
        return lon;
    }

    public void setLon(double lon)
    {
        this.lon = lon;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getCountryName()
    {
        return countryName;
    }

    public void setCountryName(String countryName)
    {
        this.countryName = countryName;
    }

    public String getCountryCode()
    {
        return countryCode;
    }

    public void setCountryCode(String countryCode)
    {
        this.countryCode = countryCode;
    }

    public int getOsmMosqueNodes()
    {
        return osmMosqueNodes;
    }

    public void setOsmMosqueNodes(int osmMosqueNodes)
    {
        this.osmMosqueNodes = osmMosqueNodes;
    }

    public int getOsmMosqueWays()
    {
        return osmMosqueWays;
    }

    public void setOsmMosqueWays(int osmMosqueWays)
    {
        this.osmMosqueWays = osmMosqueWays;
    }

    public int getOsmMosqueTotal()
    {
        return osmMosqueTotal;
    }

    public void setOsmMosqueTotal(int osmMosqueTotal)
    {
        this.osmMosqueTotal = osmMosqueTotal;
    }
}
