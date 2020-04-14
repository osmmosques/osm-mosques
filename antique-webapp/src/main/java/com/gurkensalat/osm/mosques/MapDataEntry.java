package com.gurkensalat.osm.mosques;

import com.gurkensalat.osm.entity.DitibPlace;
import com.gurkensalat.osm.entity.OsmPlace;
import com.gurkensalat.osm.mosques.entity.OsmMosquePlace;

public class MapDataEntry
{
    private String key;

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

    public MapDataEntry(OsmMosquePlace place)
    {
        this.setLat(place.getLat());
        this.setLon(place.getLon());
        this.setName(place.getName());
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
}
