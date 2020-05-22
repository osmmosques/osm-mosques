package com.gurkensalat.osm.mosques;

import com.gurkensalat.osm.entity.OsmPlace;
import com.gurkensalat.osm.mosques.entity.OsmMosquePlace;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Data
@NoArgsConstructor
public class MapDataEntry
{
    private String key;

    private double lat;

    private double lon;

    private String name;

    public MapDataEntry(OsmPlace place)
    {
        this.setLat(place.getLat());
        this.setLon(place.getLon());
        this.setKey(place.getKey());
        this.setName(place.getName());
    }

    public MapDataEntry(OsmMosquePlace place)
    {
        this.setLat(place.getLat());
        this.setLon(place.getLon());
        this.setKey(place.getKey());
        this.setName(place.getName());
    }
}
