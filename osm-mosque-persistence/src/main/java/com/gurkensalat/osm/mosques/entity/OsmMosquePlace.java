package com.gurkensalat.osm.mosques.entity;

import com.gurkensalat.osm.entity.Address;
import com.gurkensalat.osm.entity.Contact;
import com.gurkensalat.osm.entity.OsmNode;
import com.gurkensalat.osm.entity.OsmPlaceBase;
import com.gurkensalat.osm.entity.OsmWay;
import com.gurkensalat.osm.entity.PlaceType;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "MOSQUE_PLACES")
public class OsmMosquePlace extends OsmPlaceBase
{
    // Adapted from osmconvert, see
    // http://wiki.openstreetmap.org/wiki/Osmconvert#Dispose_of_Ways_and_Relations_and_Convert_them_to_Nodes
    private transient static final long WAY_OFFSET = (long) Math.pow(10, 15);

    @Column(name = "ADDR_COUNTRY_DATAFILE", length = 80)
    private String countryFromDatafile;

    @Column(name = "ADDR_POSTCODE_DATAFILE", length = 10)
    private String postcodeFromDatafile;

    @Column(name = "ADDR_CITY_DATAFILE", length = 80)
    private String cityFromDatafile;

    @Column(name = "ADDR_COUNTRY_OSM", length = 80)
    private String countryFromOSM;

    @Column(name = "ADDR_POSTCODE_OSM", length = 10)
    private String postcodeFromOSM;

    @Column(name = "ADDR_CITY_OSM", length = 80)
    private String cityFromOSM;

    @Column(name = "ADDR_COUNTRY_GEOCODING", length = 80)
    private String countryFromGeocoding;

    @Column(name = "ADDR_POSTCODE_GEOCODING", length = 10)
    private String postcodeFromGeocoding;

    @Column(name = "ADDR_CITY_GEOCODING", length = 80)
    private String cityFromGeocoding;

    @Column(name = "LAST_GEOCODE_ATTEMPT")
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    private DateTime lastGeocodeAttempt;

    @Transient
    private String humanReadableLastGeocodeAttempt;

    @Column(name = "QA_SCORE")
    private double qaScore;

    protected OsmMosquePlace()
    {
    }

    public OsmMosquePlace(String name, PlaceType type)
    {
        super(name, type);
    }

    public OsmMosquePlace(OsmNode node)
    {
        super(node);
    }

    public OsmMosquePlace(OsmWay way)
    {
        super(way);
    }

    public static long getWayOffset()
    {
        return WAY_OFFSET;
    }

    public boolean isOsmNode()
    {
        return ((getId() != null) && (WAY_OFFSET > getId()));
    }

    public boolean isOsmWay()
    {
        return ((getId() != null) && (WAY_OFFSET < getId()));
    }

    public String getCountryFromDatafile()
    {
        return countryFromDatafile;
    }

    public void setCountryFromDatafile(String countryFromDatafile)
    {
        this.countryFromDatafile = countryFromDatafile;
    }

    public String getPostcodeFromDatafile()
    {
        return postcodeFromDatafile;
    }

    public void setPostcodeFromDatafile(String postcodeFromDatafile)
    {
        this.postcodeFromDatafile = postcodeFromDatafile;
    }

    public String getCityFromDatafile()
    {
        return cityFromDatafile;
    }

    public void setCityFromDatafile(String cityFromDatafile)
    {
        this.cityFromDatafile = cityFromDatafile;
    }

    public String getCountryFromOSM()
    {
        return countryFromOSM;
    }

    public void setCountryFromOSM(String countryFromOSM)
    {
        this.countryFromOSM = countryFromOSM;
    }

    public String getPostcodeFromOSM()
    {
        return postcodeFromOSM;
    }

    public void setPostcodeFromOSM(String postcodeFromOSM)
    {
        this.postcodeFromOSM = postcodeFromOSM;
    }

    public String getCityFromOSM()
    {
        return cityFromOSM;
    }

    public void setCityFromOSM(String cityFromOSM)
    {
        this.cityFromOSM = cityFromOSM;
    }

    public String getCountryFromGeocoding()
    {
        return countryFromGeocoding;
    }

    public void setCountryFromGeocoding(String countryFromGeocoding)
    {
        this.countryFromGeocoding = countryFromGeocoding;
    }

    public String getPostcodeFromGeocoding()
    {
        return postcodeFromGeocoding;
    }

    public void setPostcodeFromGeocoding(String postcodeFromGeocoding)
    {
        this.postcodeFromGeocoding = postcodeFromGeocoding;
    }

    public String getCityFromGeocoding()
    {
        return cityFromGeocoding;
    }

    public void setCityFromGeocoding(String cityFromGeocoding)
    {
        this.cityFromGeocoding = cityFromGeocoding;
    }

    public DateTime getLastGeocodeAttempt()
    {
        return lastGeocodeAttempt;
    }

    public void setLastGeocodeAttempt(DateTime lastGeocodeAttempt)
    {
        this.lastGeocodeAttempt = lastGeocodeAttempt;
    }

    public String getHumanReadableLastGeocodeAttempt()
    {
        return humanReadableLastGeocodeAttempt;
    }

    public void setHumanReadableLastGeocodeAttempt(String humanReadableLastGeocodeAttempt)
    {
        this.humanReadableLastGeocodeAttempt = humanReadableLastGeocodeAttempt;
    }

    public double getQaScore()
    {
        return qaScore;
    }

    public void setQaScore(double qaScore)
    {
        this.qaScore = qaScore;
    }

    public void copyTo(OsmMosquePlace other)
    {
        other.setLon(getLon());
        other.setLat(getLat());
        other.setName(getName());
        other.setType(getType());
        other.setKey(getKey());
        other.setValid(isValid());

        other.setCountryFromDatafile(getCountryFromDatafile());
        other.setPostcodeFromDatafile(getPostcodeFromDatafile());
        other.setCityFromDatafile(getCityFromDatafile());

        other.setCountryFromGeocoding(getCountryFromGeocoding());
        other.setPostcodeFromGeocoding(getPostcodeFromGeocoding());
        other.setCityFromGeocoding(getCityFromGeocoding());

        other.setCountryFromOSM(getCountryFromOSM());
        other.setPostcodeFromOSM(getPostcodeFromOSM());
        other.setCityFromOSM(getCityFromOSM());

        other.setLastGeocodeAttempt(getLastGeocodeAttempt());
        other.setHumanReadableLastGeocodeAttempt(getHumanReadableLastGeocodeAttempt());

        other.setQaScore(getQaScore());

        if (getAddress() == null)
        {
            setAddress(new Address());
        }

        if (other.getAddress() == null)
        {
            other.setAddress(new Address());
        }

        getAddress().copyTo(other.getAddress());

        if (getContact() == null)
        {
            setContact(new Contact());
        }

        if (other.getContact() == null)
        {
            other.setContact(new Contact());
        }

        getContact().copyTo(other.getContact());
    }

}
