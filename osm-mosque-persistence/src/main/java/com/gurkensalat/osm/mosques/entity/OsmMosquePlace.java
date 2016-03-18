package com.gurkensalat.osm.mosques.entity;

import com.gurkensalat.osm.entity.Address;
import com.gurkensalat.osm.entity.Contact;
import com.gurkensalat.osm.entity.OsmNode;
import com.gurkensalat.osm.entity.OsmPlaceBase;
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
    @Column(name = "ADDR_COUNTRY_DATAFILE", length = 80)
    private String countryFromDatafile;

    @Column(name = "ADDR_COUNTRY_OSM", length = 80)
    private String countryFromOSM;

    @Column(name = "ADDR_COUNTRY_GEOCODING", length = 80)
    private String countryFromGeocoding;

    @Column(name = "LAST_GEOCODE_ATTEMPT")
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    private DateTime lastGeocodeAttempt;

    @Transient
    private String humanReadableLastGeocodeAttempt;


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

    public String getCountryFromDatafile()
    {
        return countryFromDatafile;
    }

    public void setCountryFromDatafile(String countryFromDatafile)
    {
        this.countryFromDatafile = countryFromDatafile;
    }

    public String getCountryFromOSM()
    {
        return countryFromOSM;
    }

    public void setCountryFromOSM(String countryFromOSM)
    {
        this.countryFromOSM = countryFromOSM;
    }

    public String getCountryFromGeocoding()
    {
        return countryFromGeocoding;
    }

    public void setCountryFromGeocoding(String countryFromGeocoding)
    {
        this.countryFromGeocoding = countryFromGeocoding;
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

    public void copyTo(OsmMosquePlace other)
    {
        other.setLon(getLon());
        other.setLat(getLat());
        other.setName(getName());
        other.setType(getType());
        other.setKey(getKey());
        other.setValid(isValid());

        other.setCountryFromDatafile(getCountryFromDatafile());
        other.setCountryFromGeocoding(getCountryFromGeocoding());
        other.setCountryFromOSM(getCountryFromOSM());

        other.setLastGeocodeAttempt(getLastGeocodeAttempt());
        other.setHumanReadableLastGeocodeAttempt(getHumanReadableLastGeocodeAttempt());

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
