package com.gurkensalat.osm.mosques;

import com.gurkensalat.osm.mosques.entity.StatisticsEntry;

public class MapStatisticsDataEntry
{
    private double lat;

    private double lon;

    private String name;

    private String countryCode;

    private String countryName;

    private int osmMosqueNodes;

    public MapStatisticsDataEntry(StatisticsEntry statisticsEntry)
    {
        this.setLat(statisticsEntry.getCentroidLat());
        this.setLon(statisticsEntry.getCentroidLon());
        this.setName(statisticsEntry.getCountryName());
        this.setCountryName(statisticsEntry.getCountryName());
        this.setCountryCode(statisticsEntry.getCountryName());
        this.setOsmMosqueNodes(statisticsEntry.getOsmMosqueNodes());
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
}
