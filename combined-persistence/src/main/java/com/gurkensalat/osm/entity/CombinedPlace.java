package com.gurkensalat.osm.entity;

import org.springframework.data.jpa.domain.AbstractPersistable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "DITIB_PLACES")
public class CombinedPlace extends AbstractPersistable<Long>
{
    @Column(name = "VERSION")
    private Integer version;

    @Column(name = "score")
    private double score;

    @Column(name = "osm_id", length = 80)
    private String osmId;

    private transient OsmPlace osmPlace;

    @Column(name = "ditib_key", length = 80)
    private String ditibKey;

    private transient DitibPlace ditibPlace;

    @Column(name = "cagedata_key", length = 80)
    private String geocodedPlaceKey;

    public Integer getVersion()
    {
        return version;
    }

    public void setVersion(Integer version)
    {
        this.version = version;
    }

    public double getScore()
    {
        return score;
    }

    public void setScore(double score)
    {
        this.score = score;
    }

    public String getOsmId()
    {
        return osmId;
    }

    public void setOsmId(String osmId)
    {
        this.osmId = osmId;
    }

    public OsmPlace getOsmPlace()
    {
        return osmPlace;
    }

    public void setOsmPlace(OsmPlace osmPlace)
    {
        this.osmPlace = osmPlace;
    }

    public String getDitibKey()
    {
        return ditibKey;
    }

    public void setDitibKey(String ditibKey)
    {
        this.ditibKey = ditibKey;
    }

    public DitibPlace getDitibPlace()
    {
        return ditibPlace;
    }

    public void setDitibPlace(DitibPlace ditibPlace)
    {
        this.ditibPlace = ditibPlace;
    }

    public String getGeocodedPlaceKey()
    {
        return geocodedPlaceKey;
    }

    public void setGeocodedPlaceKey(String geocodedPlaceKey)
    {
        this.geocodedPlaceKey = geocodedPlaceKey;
    }
}
