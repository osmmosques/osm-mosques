package com.gurkensalat.osm.entity;

import com.gurkensalat.osm.mosques.entity.OsmMosquePlace;
import org.springframework.data.jpa.domain.AbstractPersistable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "QA_PLACES")
public class LinkedPlace extends AbstractPersistable<Long>
{
    @Column(name = "VERSION")
    private Integer version;

    @Column(name = "VALID")
    private boolean valid;

    private transient double lat;

    private transient double lon;

    @Column(name = "OSM_ID", length = 20)
    private String osmId;

    private transient OsmMosquePlace osmMosquePlace;

    @Column(name = "DITIB_CODE", length = 20)
    private String ditibCode;

    private transient DitibPlace ditibPlace;

    @Column(name = "CAGEDATA_KEY", length = 20)
    private String geocodedPlaceKey;

    @Column(name = "SCORE")
    private double score;

    @Column(name = "BD_GEOCODED")
    private int badnessIsGeocoded;

    public Integer getVersion()
    {
        return version;
    }

    public void setVersion(Integer version)
    {
        this.version = version;
    }

    public boolean isValid()
    {
        return valid;
    }

    public void setValid(boolean valid)
    {
        this.valid = valid;
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

    public String getOsmId()
    {
        return osmId;
    }

    public void setOsmId(String osmId)
    {
        this.osmId = osmId;
    }

    public OsmMosquePlace getOsmMosquePlace()
    {
        return osmMosquePlace;
    }

    public void setOsmMosquePlace(OsmMosquePlace osmMosquePlace)
    {
        this.osmMosquePlace = osmMosquePlace;
    }

    public String getDitibCode()
    {
        return ditibCode;
    }

    public void setDitibCode(String ditibCode)
    {
        this.ditibCode = ditibCode;
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

    public double getScore()
    {
        return score;
    }

    public void setScore(double score)
    {
        this.score = score;
    }

    public int getBadnessIsGeocoded()
    {
        return badnessIsGeocoded;
    }

    public void setBadnessIsGeocoded(int badnessIsGeocoded)
    {
        this.badnessIsGeocoded = badnessIsGeocoded;
    }
}
