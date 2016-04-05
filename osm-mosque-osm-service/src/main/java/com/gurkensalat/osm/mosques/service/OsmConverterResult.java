package com.gurkensalat.osm.mosques.service;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.joda.time.DateTime;

public class OsmConverterResult
{
    String what;

    String path;

    DateTime start;

    DateTime end;

    int places;

    int nodes;

    int ways;

    public String getWhat()
    {
        return what;
    }

    public void setWhat(String what)
    {
        this.what = what;
    }

    public String getPath()
    {
        return path;
    }

    public void setPath(String path)
    {
        this.path = path;
    }

    public DateTime getStart()
    {
        return start;
    }

    public void setStart(DateTime start)
    {
        this.start = start;
    }

    public DateTime getEnd()
    {
        return end;
    }

    public void setEnd(DateTime end)
    {
        this.end = end;
    }

    public int getPlaces()
    {
        return places;
    }

    public void setPlaces(int places)
    {
        this.places = places;
    }

    public int getNodes()
    {
        return nodes;
    }

    public void setNodes(int nodes)
    {
        this.nodes = nodes;
    }

    public int getWays()
    {
        return ways;
    }

    public void setWays(int ways)
    {
        this.ways = ways;
    }

    public String toString()
    {
        return (new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE))
                .append("what", what)
                .append("path", path)
                .append("places", places)
                .append("nodes", nodes)
                .append("ways", ways)
                .append("start", start)
                .append("end", end)
                .toString();
    }

}
