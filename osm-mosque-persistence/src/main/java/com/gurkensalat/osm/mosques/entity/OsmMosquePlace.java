package com.gurkensalat.osm.mosques.entity;

import com.gurkensalat.osm.entity.Address;
import com.gurkensalat.osm.entity.Contact;
import com.gurkensalat.osm.entity.OsmNode;
import com.gurkensalat.osm.entity.OsmPlaceBase;
import com.gurkensalat.osm.entity.OsmWay;
import com.gurkensalat.osm.entity.PlaceType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name = "MOSQUE_PLACES")
@Getter
@Setter
public class OsmMosquePlace extends OsmPlaceBase
{
    @Column(name = "TEMP_NAME", length = 80)
    private String name;

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
    private LocalDateTime lastGeocodeAttempt;

    @Transient
    private String humanReadableLastGeocodeAttempt;

    @Column(name = "QA_SCORE")
    private double qaScore;

    // Adapted from osmconvert, see
    // http://wiki.openstreetmap.org/wiki/Osmconvert#Dispose_of_Ways_and_Relations_and_Convert_them_to_Nodes
    private transient static final long WAY_OFFSET = (long) Math.pow(10, 15);

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
