package com.gurkensalat.osm.mosques.entity;

import com.gurkensalat.osm.entity.OsmPlaceBase;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

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
}
