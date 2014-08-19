package com.gurkensalat.osm.mosques;

import com.gurkensalat.osm.entity.DitibPlace;
import com.gurkensalat.osm.entity.OsmPlace;

public class MapDataEntry
{
    private double lat;

    private double lon;

    private String name;

    public MapDataEntry(DitibPlace place)
    {
        this.setLat(place.getLat());
        this.setLon(place.getLon());
        this.setName(place.getName());
    }

    public MapDataEntry(OsmPlace place)
    {
        this.setLat(place.getLat());
        this.setLon(place.getLon());
        this.setName(place.getName());
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
}
